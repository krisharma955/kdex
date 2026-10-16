package com.k955.kdex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KdexApplication {

	public static void main(String[] args) {
		SpringApplication.run(KdexApplication.class, args);
	}

}
