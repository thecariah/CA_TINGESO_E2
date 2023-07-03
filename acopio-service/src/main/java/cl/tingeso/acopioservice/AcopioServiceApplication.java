package cl.tingeso.acopioservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class}, scanBasePackages={
		"cl.tingeso.acopioservice.repositories"})
@EnableEurekaClient
public class AcopioServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcopioServiceApplication.class, args);
	}

}
