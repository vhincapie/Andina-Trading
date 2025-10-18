package co.edu.unbosque.foresta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "co.edu.unbosque.foresta.integration")
@EnableScheduling
public class ForestaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForestaApplication.class, args);
	}

}
