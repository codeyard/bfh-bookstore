package ch.rgis.bookshipping.service;

import ch.rgis.bookshipping.dto.ShippingOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Component
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Async
    public void sendSimpleMessage(ShippingOrder order) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bookstore.gruppe1@gmail.com");
        message.setTo(order.getCustomer().email().toLowerCase(Locale.ROOT));
        message.setSubject("Your order at the Bookstore");

        String messageBody = """
      
                Many thanks for your order.
                Your order is about to be shipped and you will receive it in the next days.
                
                Do not hesitate to contact us, if you have any questions.
                
                Best wishes,
                
                The Bookstore
                """;

        String text = String.format("Dear %s,", order.getCustomer().firstName());
        message.setText(text + "\n" + messageBody);

        mailSender.send(message);

        System.out.println("EMAIL SENT....");

    }


}
