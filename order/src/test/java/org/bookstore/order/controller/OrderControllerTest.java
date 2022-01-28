package org.bookstore.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bookstore.customer.entity.Address;
import org.bookstore.customer.entity.CreditCard;
import org.bookstore.customer.entity.CreditCardType;
import org.bookstore.customer.entity.Customer;
import org.bookstore.customer.exception.CustomerNotFoundException;
import org.bookstore.order.adapter.CatalogAdapter;
import org.bookstore.order.adapter.PaymentAdapter;
import org.bookstore.order.entity.*;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @MockBean
    private CatalogAdapter catalogAdapter;
    @MockBean
    private PaymentAdapter paymentAdapter;


//        @Test
//        void getBook_bookNotFound() throws Exception {
//            Mockito.when(catalogService.findBook(any())).thenThrow(new BookNotFoundException("Book not found"));
//            mockMvc.perform(get(BASE_PATH + "/1111111111"))
//                    .andExpect(status().isNotFound());
//        }
//
//        @Test
//        void findBooks_noneFound() throws Exception {
//            Mockito.when(catalogService.searchBooks(any())).thenReturn(new ArrayList<>());
//            mockMvc.perform(get(BASE_PATH + "?keywords=asdfasdfadsfasfd"))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(APPLICATION_JSON))
//                    .andExpect(jsonPath("$").isEmpty());
//        }
//
//        @Test
//        void addBook_bookAdded() throws Exception {
//            String newIsbn = "1234567890";
//            Book book = createBook();
//            book.setIsbn(newIsbn);
//            Mockito.when(catalogService.addBook(any())).thenReturn(book);
//            mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(book)))
//                    .andExpect(status().isCreated())
//                    .andExpect(content().contentType(APPLICATION_JSON))
//                    .andExpect(jsonPath("isbn").value(newIsbn))
//                    .andExpect(jsonPath("title").value("Flower Power Letterings"));
//        }



    @Nested
    class withCorrectSetUp {
        @BeforeEach
        public void configureMockBean() throws CustomerNotFoundException {
            Order order = createOrder();
            Mockito.when(orderService.prepareOrder(anyLong(), any())).thenReturn(order);
        }

        @Test
        void placeOrder_succesful() throws Exception {
            Order order = createOrder();
            mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(orderRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("id").value(order.getId()))
                    .andExpect(jsonPath("amount").value(order.getAmount()));
        }



    }

    @Nested
    class withCustomerNotFound {
        @BeforeEach
        public void configureMockBean() throws CustomerNotFoundException {
            Mockito.when(orderService.prepareOrder(anyLong(), any())).thenThrow(new CustomerNotFoundException(213L));
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
        void placeOrder_ThrowsCustomerNotFoundException() throws Exception {
            mockMvc.perform(post(BASE_PATH).contentType(APPLICATION_JSON).content(asJson(orderRequest())))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("code").value("INVALID_CREDIT_CARD"))
                    .andExpect(jsonPath("path").value("/orders"))
                    .andExpect(jsonPath("message").value("Invalid credit card number or type"))
                    .andExpect(jsonPath("error").value("Unprocessable Entity"));
        }
    }

//            @Test
//            void getBook_invalidIsbn() throws Exception {
//                mockMvc.perform(get(BASE_PATH + "/12345"))
//                        .andExpect(status().isBadRequest());
//            }
//
//            @Test
//            void findBooks_foundOne() throws Exception {
//                mockMvc.perform(get(BASE_PATH + "?keywords=flower power"))
//                        .andExpect(status().isOk())
//                        .andExpect(content().contentType(APPLICATION_JSON))
//                        .andExpect(jsonPath("$").isNotEmpty())
//                        .andExpect(jsonPath("$..title").value("Flower Power Letterings"));
//            }

    private String asJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    private OrderRequest orderRequest() {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(99951L);
        OrderRequest.Item orderItem = new OrderRequest.Item();
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


}


