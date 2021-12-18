package ch.rgis.bookshipping;

import ch.rgis.bookshipping.dto.ShippingInfo;
import ch.rgis.bookshipping.dto.ShippingOrder;
import ch.rgis.bookshipping.service.ShippingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ShippingServiceIT {

    @Autowired
    private ShippingService shippingService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${shipping.order-queue}")
    private String orderQueue;
    @Value("${shipping.cancel-queue}")
    private String cancelQueue;
    @Value("${shipping.info-queue}")
    private String infoQueue;


    @Test
    void receiveShippingOrder_successful()  {
        ShippingOrder shippingOrder = new ShippingOrder();
        shippingOrder.setOrderId(100000L);
        jmsTemplate.convertAndSend(orderQueue, shippingOrder);

        ShippingInfo processingMessage = (ShippingInfo) jmsTemplate.receiveAndConvert(infoQueue);
        Assertions.assertEquals(ShippingOrder.OrderStatus.PROCESSING, processingMessage.getStatus());

        ShippingInfo shippingMessage = (ShippingInfo) jmsTemplate.receiveAndConvert(infoQueue);
        Assertions.assertEquals(ShippingOrder.OrderStatus.SHIPPED, shippingMessage.getStatus());
    }

    @Test
    void cancelShippingOrder_successful() {
        jmsTemplate.convertAndSend(cancelQueue, 100000L);

        ShippingInfo cancelMessage = (ShippingInfo) jmsTemplate.receiveAndConvert(infoQueue);
        Assertions.assertEquals(ShippingOrder.OrderStatus.CANCELED, cancelMessage.getStatus());
    }

}
