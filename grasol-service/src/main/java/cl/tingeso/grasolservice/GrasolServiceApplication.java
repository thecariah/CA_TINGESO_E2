package cl.tingeso.grasolservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class GrasolServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrasolServiceApplication.class, args);
	}

}
