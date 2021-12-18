package ch.rgis.bookshipping;

import ch.rgis.bookshipping.dto.ShippingInfo;
import ch.rgis.bookshipping.dto.ShippingOrder;
import ch.rgis.bookshipping.service.ShippingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import java.io.IOException;

import static org.junit.Assert.*;

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
    void receiveShippingOrder_successful() throws JMSException, IOException {
        //ShippingOrder shippingOrder = Mockito.mock(ShippingOrder.class);
        //Mockito.when(shippingOrder.getOrderId()).thenReturn(100000L);

        ShippingOrder shippingOrder = new ShippingOrder();
        shippingOrder.setOrderId(100000L);

        jmsTemplate.send(orderQueue, session -> {
            try {
                String content = new ObjectMapper().writeValueAsString(shippingOrder);
                return session.createTextMessage(content);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        ShippingInfo response = new ObjectMapper().readValue((byte[]) jmsTemplate.receiveAndConvert(infoQueue), ShippingInfo.class);
    }
}
