package ch.rgis.bookorders.shipping.service;

import ch.rgis.bookorders.order.entity.Order;
import ch.rgis.bookorders.order.repository.OrderRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class EmailService {

    private final JavaMailSender mailSender;

    private final OrderRepository orderRepository;


    public EmailService(JavaMailSender mailSender, OrderRepository orderRepository) {
        this.mailSender = mailSender;
        this.orderRepository = orderRepository;
    }

    @Async
    public void sendSimpleMessage(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        optionalOrder.ifPresent(order -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bookstore.gruppe1@gmail.com");
            message.setTo(order.getCustomer().getEmail().toLowerCase(Locale.ROOT));
            message.setSubject("Your order at the Bookstore");

            StringBuilder builder = new StringBuilder();
            builder.append(String.format("Dear %s,", order.getCustomer().getFirstName()));
            builder.append("\n");

            StringBuilder finalMessage = new StringBuilder();

            switch (order.getStatus()) {
                case CANCELED ->  finalMessage = buildCanceledMessage(builder, order.getId());
                case PROCESSING -> finalMessage = buildProcessingMessage(builder);
                case SHIPPED -> finalMessage = buildShippedMessage(builder, order);
            }

            message.setText(finalMessage.toString());
            mailSender.send(message);
            System.out.println("EMAIL SENT....");

        });
    }

    private StringBuilder buildProcessingMessage(StringBuilder builder) {
        return builder.append(
                """
                                            
                        Many thanks for your order.
                        
                        We are about to process your order and it is being shipped soon.
                                            
                        We will notify you as soon at the shipment is made.
                                            
                        Do not hesitate to contact us, if you have any questions.
                                        
                        Best wishes,
                                        
                        The Bookstore
                                            
                        """);
    }

    private StringBuilder buildCanceledMessage(StringBuilder builder, Long orderId) {
        return builder.append(String.format("""    
                                                     
                    Many thanks for your cancellation.
                    We confirm that the following order was canceled: %s
                    
                    Do not hesitate to contact us, if you have any questions.
                                    
                    Best wishes,
                                    
                    The Bookstore
                                        
                    """, orderId));
    }

    private StringBuilder buildShippedMessage(StringBuilder builder, Order order) {
        builder.append("""
                                        
                    Many thanks for your order.
                    
                    Your order is about to be shipped and you will receive it in the next days. It contains the following Items:
                                        
                    """);

        order.getItems().forEach(orderItem ->
                builder.append(orderItem.getQuantity()).append("x").append(" ISBN:")
                        .append(orderItem.getBook().getIsbn()).append(" ")
                        .append(orderItem.getBook().getTitle()).append("\n"));

        builder.append("""
                        
                    Do not hesitate to contact us, if you have any questions.
                                    
                    Best wishes,
                                    
                    The Bookstore
                    """);

        return builder;
    }


}
