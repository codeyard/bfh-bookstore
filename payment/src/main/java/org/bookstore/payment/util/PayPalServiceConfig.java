package org.bookstore.payment.util;

import ebay.api.paypalapi.PayPalAPIAAInterface;
import ebay.api.paypalapi.PayPalAPIInterfaceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayPalServiceConfig {


    @Bean
    public PayPalAPIAAInterface payPalService() {
        System.out.println("SETTING UP BEAN PAYPAL SERVICE");
        PayPalAPIInterfaceService service = new PayPalAPIInterfaceService();
        return service.getPort(PayPalAPIAAInterface.class);
    }

}
