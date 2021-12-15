package ch.rgis.bookorders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class BookOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookOrdersApplication.class, args);
    }

}
