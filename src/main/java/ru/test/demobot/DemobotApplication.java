package ru.test.demobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.test.demobot.database.entites.Numbers;
import ru.test.demobot.database.repository.NumberRepository;


@SpringBootApplication
public class DemobotApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemobotApplication.class, args);

	}
}
