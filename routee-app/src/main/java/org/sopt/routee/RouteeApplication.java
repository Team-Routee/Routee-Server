package org.sopt.routee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RouteeApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouteeApplication.class, args);
	}
}
