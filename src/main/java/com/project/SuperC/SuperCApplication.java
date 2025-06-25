package com.project.SuperC;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv; // Import the Dotenv class

@SpringBootApplication
public class SuperCApplication {

	public static void main(String[] args) {
		// Load the .env file
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMalformed() // Optional: Don't throw an error if .env has syntax issues
				.ignoreIfMissing()   // Optional: Don't throw an error if .env file is missing
				.load();

		// Iterate over the loaded properties and set them as System properties
		// Spring Boot can then pick these up
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		// Now, run the Spring Boot application
		SpringApplication.run(SuperCApplication.class, args);
	}
}