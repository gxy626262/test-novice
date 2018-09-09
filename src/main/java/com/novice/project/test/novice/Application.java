package com.novice.project.test.novice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(com.novice.framework.ProjectConfig.class)
public class Application {
	public static void main(String[] args) {
		var app = new SpringApplication(Application.class);
		app.run(args);
	}
}
