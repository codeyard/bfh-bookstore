package ch.rgis.bookorders.bookshipping;

import ch.rgis.bookorders.bookshipping.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ShippingServiceIT {

    @Autowired
    private ShippingService shippingService;
}
