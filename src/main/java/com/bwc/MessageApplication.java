package com.bwc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class MessageApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MessageApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
