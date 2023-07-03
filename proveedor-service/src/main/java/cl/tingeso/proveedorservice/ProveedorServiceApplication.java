package cl.tingeso.proveedorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class}, scanBasePackages={
		"cl.tingeso.proveedorservice.repositories"})
@EnableEurekaClient
public class ProveedorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProveedorServiceApplication.class, args);
	}

}
