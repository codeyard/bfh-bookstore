package org.bookstore.payment;

import ebay.api.paypalapi.PayPalAPIAAInterface;
import ebay.api.paypalapi.PayPalAPIInterfaceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class PayPalServiceConfig {

    @Value("paypal.wsdlUrl;")
    private String wsdlUrl;

    @Bean
    public PayPalAPIAAInterface payPalService() throws MalformedURLException {
        System.out.println("SETTING UP BEAN PAYPAL SERVICE");
        PayPalAPIInterfaceService service = new PayPalAPIInterfaceService(new URL("https://www.paypalobjects.com/wsdl/PayPalSvc.wsdl"));
        return service.getPayPalAPIAA();
    }

}
