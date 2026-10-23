package io.github.mikeopima.bookedly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BookedlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookedlyApplication.class, args);
	}

}
