package org.pn.igest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class IGestApplication {

	public static void main(String[] args) {
		SpringApplication.run(IGestApplication.class, args);
	}

}