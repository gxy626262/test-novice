package com.novice.project.test;

import com.novice.framework.datamodel.annotation.MetaScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MetaScan
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		var app = new SpringApplication(Application.class);
		app.run(args);
	}

}
