package cl.tingeso.grasolservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class}, scanBasePackages={
		"cl.tingeso.grasolservice.repositories"})
@EnableEurekaClient
public class GrasolServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrasolServiceApplication.class, args);
	}

}
