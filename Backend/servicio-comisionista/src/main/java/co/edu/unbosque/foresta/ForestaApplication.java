package co.edu.unbosque.foresta;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableFeignClients(basePackages = "co.edu.unbosque.foresta.integration")
public class ForestaApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(ForestaApplication.class, args);
    }
}
