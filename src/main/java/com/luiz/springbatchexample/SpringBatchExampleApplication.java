package com.luiz.springbatchexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * exclude = DataSourceAutoConfiguration.class means that all batch control is gonna be on memory
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SpringBatchExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchExampleApplication.class, args);
	}

}
