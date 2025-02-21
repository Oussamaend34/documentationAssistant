package com.ensah.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;
@CommandScan
@SpringBootApplication
public class DocumentationAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentationAssistantApplication.class, args);
	}

}
