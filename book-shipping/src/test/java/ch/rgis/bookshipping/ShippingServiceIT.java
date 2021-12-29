package ch.rgis.bookshipping;

import ch.rgis.bookshipping.dto.ShippingInfo;
import ch.rgis.bookshipping.dto.ShippingOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

@SpringBootTest
public class ShippingServiceIT {

    @Autowired
    private JmsTemplate jmsTemplate;


    @Value("${shipping.order-queue}")
    private String orderQueue;
    @Value("${shipping.cancel-queue}")
    private String cancelQueue;
    @Value("${shipping.info-queue}")
    private String infoQueue;


    @Test
    void receiveShippingOrder_successful() {
        ShippingOrder shippingOrder = new ShippingOrder();
        shippingOrder.setOrderId(100000L);

        ShippingOrder.Customer customer = new ShippingOrder.Customer(1000L, "Igor", "Stojanovic", "i.stojanovic01@gmail.com");
        shippingOrder.setCustomer(customer);
        jmsTemplate.convertAndSend(orderQueue, shippingOrder);

        ShippingInfo processingMessage = (ShippingInfo) jmsTemplate.receiveAndConvert(infoQueue);
        Assertions.assertEquals(ShippingOrder.OrderStatus.PROCESSING, processingMessage.getStatus());

        ShippingInfo shippingMessage = (ShippingInfo) jmsTemplate.receiveAndConvert(infoQueue);
        Assertions.assertEquals(ShippingOrder.OrderStatus.SHIPPED, shippingMessage.getStatus());
    }


    @Test
    void cancelShippingOrder_successful() {
        ShippingOrder shippingOrder = new ShippingOrder();
        shippingOrder.setOrderId(100001L);

        System.out.println("SENDING ORDER....");
        ShippingOrder.Customer customer = new ShippingOrder.Customer(1000L, "Igor", "Stojanovic", "i.stojanovic01@gmail.com");
        shippingOrder.setCustomer(customer);
        jmsTemplate.convertAndSend(orderQueue, shippingOrder);

        ShippingInfo processingMessage = (ShippingInfo) jmsTemplate.receiveAndConvert(infoQueue);
        Assertions.assertEquals(ShippingOrder.OrderStatus.PROCESSING, processingMessage.getStatus());

        System.out.println("SENDING CANCELLATION....");
        jmsTemplate.convertAndSend(cancelQueue, 100001L);
        ShippingInfo cancelMessage = (ShippingInfo) jmsTemplate.receiveAndConvert(infoQueue);
        Assertions.assertEquals(ShippingOrder.OrderStatus.CANCELED, cancelMessage.getStatus());
    }

}
