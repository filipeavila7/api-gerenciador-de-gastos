package com.example.gerenciador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class GerenciadorApplication {



	public static void main(String[] args) {

		TimeZone.setDefault(
				TimeZone.getTimeZone("America/Sao_Paulo")
		);

		SpringApplication.run(GerenciadorApplication.class, args);
	}

}
