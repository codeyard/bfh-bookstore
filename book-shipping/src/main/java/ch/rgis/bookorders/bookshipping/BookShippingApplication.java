package ch.rgis.bookorders.bookshipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class BookShippingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookShippingApplication.class, args);
	}

}
