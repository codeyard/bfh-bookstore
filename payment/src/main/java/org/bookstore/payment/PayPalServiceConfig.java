package org.bookstore.payment;

import ebay.api.paypalapi.PayPalAPIAAInterface;
import ebay.api.paypalapi.PayPalAPIInterfaceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

@Configuration
public class PayPalServiceConfig {

    // TODO CHECK WHY VALUE INJECTION DOES NOT WORK

//    @Value("${bookstore.paypal.wsdlurl}")
//    private String wsdlUrl;
//    @Value("${bookstore.paypal.endpoint}")
//    private String endpoint;


    @Bean
    public PayPalAPIAAInterface payPalService() throws MalformedURLException {
        URL wsdl = new URL("https://www.paypalobjects.com/wsdl/PayPalSvc.wsdl");
        PayPalAPIInterfaceService service = new PayPalAPIInterfaceService(wsdl);
        PayPalAPIAAInterface payPalAPIAA = service.getPayPalAPIAA();
        ((BindingProvider) payPalAPIAA).getRequestContext().put(ENDPOINT_ADDRESS_PROPERTY, "https://api-3t.sandbox.paypal.com/2.0/");
        return payPalAPIAA;
    }

}
