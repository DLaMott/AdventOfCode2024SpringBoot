package com.advent.AdventOfCode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class AdventOfCodeApplication {

	private static Logger logger = LoggerFactory.getLogger(AdventOfCodeApplication.class);

	public static void main(String[] args) {

		try{
			ConfigurableApplicationContext ac = SpringApplication.run(AdventOfCodeApplication.class, args);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				logger.info("SIGTERM issued");
				ac.close();
				logger.info("Exit");
			}
			));
		} catch(Exception e) {
			logger.error("Error Starting spring", e);
		}
	}


}
