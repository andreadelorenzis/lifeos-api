package com.andreadelorenzis.productivityApp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "Productivity App API",
		version = "0.0.1",
		description = "RESTful API for managing goals, tasks, and recurring habits",
		contact = @Contact(
			name = "Andrea De Lorenzis",
			email = "andreadelorenzis99@gmail.com"
		),
		license = @License(
			name = "MIT",
			url = "https://opensource.org/licenses/MIT"
		)
	),
	servers = {
		@Server(
			url = "http://localhost:8080",
			description = "Local Development Server"
		),
		@Server(
			url = "http://localhost:5432",
			description = "PostgreSQL Database Server"
		)
	}
)
public class ProductivityAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductivityAppApplication.class, args);
	}

}
