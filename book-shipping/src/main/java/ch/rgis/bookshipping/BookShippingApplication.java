package ch.rgis.bookshipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJms
@EnableAsync
public class BookShippingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookShippingApplication.class, args);
	}

}
