package org.bookstore.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bookstore.customer.entity.Address;
import org.bookstore.customer.entity.CreditCard;
import org.bookstore.customer.entity.CreditCardType;
import org.bookstore.customer.entity.Customer;
import org.bookstore.customer.exception.CustomerNotFoundException;
import org.bookstore.order.dto.OrderInfo;
import org.bookstore.order.entity.*;
import org.bookstore.order.exception.OrderAlreadyShippedException;
import org.bookstore.order.exception.OrderNotFoundException;
import org.bookstore.order.exception.PaymentFailedException;
import org.bookstore.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    private static final String BASE_PATH = "/orders";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderService orderService;

    @Nested
    class withCorrectSetUp {
        @BeforeEach
        public void configureMockBean() throws CustomerNotFoundException, OrderNotFoundException {
            Order order = createOrder();
            List<OrderInfo> orderInfo = createOrderInfo();
            Mockito.when(orderService.prepareOrder(anyLong(), any())).thenReturn(order);
            Mockito.when(orderService.findOrder(anyLong())).thenReturn(order);
            Mockito.when(orderService.searchOrders(anyLong(), anyInt())).thenReturn(orderInfo);
        }

        @Test
        void placeOrder_successful() throws Exception {
            Order order = createOrder();
            mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(orderRequest())))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").value(order.getId()))
                .andExpect(jsonPath("amount").value(order.getAmount()));
        }

        @Test
        void findOrder_successful() throws Exception {
            Order order = createOrder();
            mockMvc.perform(get(BASE_PATH + "/99951"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("id").value(order.getId()))
                .andExpect(jsonPath("amount").value(order.getAmount()))
                .andExpect(jsonPath("status").value("PROCESSING"));
        }

        @Test
        void searchOrders_successful() throws Exception {
            mockMvc.perform(get(BASE_PATH + "?customerId=10002&year=2022"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("1111"))
                .andExpect(jsonPath("$[0].amount").value(BigDecimal.valueOf(44.4)))
                .andExpect(jsonPath("$[0].status").value("PROCESSING"));
        }

        @Test
        void cancelOrder_successful() throws Exception {
            OrderService orderService = mock(OrderService.class);
            Mockito.doNothing().when(orderService).cancelOrder(isA(Long.class));

            mockMvc.perform(patch(BASE_PATH + "/99951"))
                .andExpect(status().isNoContent());
        }
    }

    @Nested
    class withMissingItemQuantity {

        @Test
        void placeOrder_missingItemQuantity() throws Exception {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setCustomerId(99951L);
            Item orderItem = new Item();
            orderItem.setIsbn("1234567890");
            orderRequest.setItems(List.of(orderItem));

            mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("path").value("/orders"))
                .andExpect(jsonPath("message").value("Missing item quantity"))
                .andExpect(jsonPath("error").value("Bad Request"));
        }

        @Test
        void placeOrder_itemQuantityNull() throws Exception {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setCustomerId(99951L);
            Item orderItem = new Item();
            orderItem.setIsbn("1234567890");
            orderItem.setQuantity(null);
            orderRequest.setItems(List.of(orderItem));

            mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("path").value("/orders"))
                .andExpect(jsonPath("message").value("Missing item quantity"))
                .andExpect(jsonPath("error").value("Bad Request"));
        }

        @Test
        void placeOrder_itemQuantityNegative() throws Exception {
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setCustomerId(99951L);
            Item orderItem = new Item();
            orderItem.setIsbn("1234567890");
            orderItem.setQuantity(-1);
            orderRequest.setItems(List.of(orderItem));

            mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("path").value("/orders"))
                .andExpect(jsonPath("message").value("Missing item quantity"))
                .andExpect(jsonPath("error").value("Bad Request"));
        }
    }

    @Nested
    class withCustomerNotFoundException {
        @BeforeEach
        public void configureMockBean() throws CustomerNotFoundException {
            Mockito.when(orderService.prepareOrder(anyLong(), any())).thenThrow(new CustomerNotFoundException(213L));
            Mockito.when(orderService.searchOrders(anyLong(), anyInt())).thenThrow(new CustomerNotFoundException(213L));
        }

        @Test
        void placeOrder_ThrowsCustomerNotFoundException() throws Exception {
            mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(orderRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("CUSTOMER_NOT_FOUND"))
                .andExpect(jsonPath("path").value("/orders"))
                .andExpect(jsonPath("message").value("Customer 213 not found"))
                .andExpect(jsonPath("error").value("Not Found"));
        }

        @Test
        void searchOrder_ThrowsCustomerNotFoundException() throws Exception {
            mockMvc.perform(get(BASE_PATH + "?customerId=213&year=2022"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("CUSTOMER_NOT_FOUND"))
                .andExpect(jsonPath("path").value("/orders"))
                .andExpect(jsonPath("message").value("Customer 213 not found"))
                .andExpect(jsonPath("error").value("Not Found"));
        }
    }

    @Nested
    class withPaymentFailedException {
        @BeforeEach
        public void configureMockBean() throws CustomerNotFoundException {
            ErrorInfo errorInfo = new ErrorInfo();
            errorInfo.setMessage("Invalid credit card number or type");
            errorInfo.setError("Unprocessable Entity");
            errorInfo.setStatus(HttpStatus.UNPROCESSABLE_ENTITY);
            errorInfo.setCode(ErrorCode.INVALID_CREDIT_CARD);
            Mockito.when(orderService.prepareOrder(anyLong(), any())).thenThrow(new PaymentFailedException(errorInfo));
        }

        @Test
        void placeOrder_ThrowsInvalidCreditCardException() throws Exception {
            mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(orderRequest())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("code").value("INVALID_CREDIT_CARD"))
                .andExpect(jsonPath("path").value("/orders"))
                .andExpect(jsonPath("message").value("Invalid credit card number or type"))
                .andExpect(jsonPath("error").value("Unprocessable Entity"));
        }
    }

    @Nested
    class withOrderNotFoundException {
        @BeforeEach
        public void configureMockBean() throws OrderNotFoundException {
            Mockito.when(orderService.findOrder(anyLong())).thenThrow(new OrderNotFoundException(99951L));
        }

        @Test
        void findOrder_ThrowsOrderNotFoundException() throws Exception {
            mockMvc.perform(get(BASE_PATH + "/99951"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("path").value("/orders/99951"))
                .andExpect(jsonPath("message").value("Order 99951 not found"))
                .andExpect(jsonPath("error").value("Not Found"));
        }

        @Test
        void cancelOrder_ThrowsOrderNotFoundException() throws Exception {
            doThrow(new OrderNotFoundException(99951L))
                .when(orderService)
                .cancelOrder(anyLong());
            mockMvc.perform(patch(BASE_PATH + "/99951"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("code").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("path").value("/orders/99951"))
                .andExpect(jsonPath("message").value("Order 99951 not found"))
                .andExpect(jsonPath("error").value("Not Found"));
        }
    }

    @Nested
    class withOrderAlreadyShippedException {


        @Test
        void cancelOrder_ThrowsBookAlreadyShippedException() throws Exception {
            doThrow(new OrderAlreadyShippedException(1000L))
                .when(orderService)
                .cancelOrder(anyLong());

            mockMvc.perform(patch(BASE_PATH + "/1000"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("code").value("ORDER_ALREADY_SHIPPED"))
                .andExpect(jsonPath("path").value("/orders/1000"))
                .andExpect(jsonPath("message").value("Order 1000 already shipped"))
                .andExpect(jsonPath("error").value("Conflict"));
        }

    }


    private String asJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    private OrderRequest orderRequest() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(99951L);
        Item orderItem = new Item();
        orderItem.setIsbn("1234567890");
        orderItem.setQuantity(1);
        orderRequest.setItems(List.of(orderItem));
        return orderRequest;
    }

    private Order createOrder() {
        Order order = new Order();
        order.setId(99951L);
        order.setDate(LocalDateTime.now());
        order.setAmount(BigDecimal.valueOf(44.44));
        order.setStatus(OrderStatus.PROCESSING);
        Address address = new Address();
        address.setStreet("123 Maple Street");
        address.setStateProvince("CA");
        address.setPostalCode("90952");
        address.setCity("Mill Valley");
        address.setCountry("US");
        order.setAddress(address);
        Payment payment = new Payment();
        payment.setId(951L);
        payment.setDate(LocalDateTime.now());
        payment.setAmount(BigDecimal.valueOf(44.44));
        payment.setCreditCardNumber("5400000000000005");
        payment.setTransactionId("4LN991802K8833055");
        order.setPayment(payment);
        Customer customer = new Customer();
        customer.setId(9951L);
        customer.setFirstName("Alice");
        customer.setLastName("Smith");
        customer.setEmail("@alice@example.org");
        customer.setUsername("alice");
        customer.setAddress(address);
        CreditCard creditCard = new CreditCard();
        creditCard.setType(CreditCardType.MASTER_CARD);
        creditCard.setNumber("5400000000000005");
        creditCard.setExpirationMonth(1);
        creditCard.setExpirationYear(2025);
        customer.setCreditCard(creditCard);
        order.setCustomer(customer);
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(1);
        orderItem.setId(951L);
        Book book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Sasacsa");
        book.setAuthors("ascsacsca");
        book.setPublisher("ascsacsca");
        book.setPrice(BigDecimal.valueOf(44.44));
        orderItem.setBook(book);
        order.setItems(List.of(orderItem));

        return order;
    }

    private List<OrderInfo> createOrderInfo() {
        Long id = 1111L;
        BigDecimal amount = BigDecimal.valueOf(44.4);
        OrderStatus status = OrderStatus.PROCESSING;
        OrderInfo orderInfo = new OrderInfo(id, LocalDateTime.now(), amount, status);
        return List.of(orderInfo);
    }


}


