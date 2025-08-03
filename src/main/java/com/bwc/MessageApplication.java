package com.bwc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ComponentScan;

@EnableAspectJAutoProxy
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.bwc.common",
    "com.bwc.config",
    "com.bwc.messaging"
})
public class MessageApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MessageApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

}
