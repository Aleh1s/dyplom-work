package ua.aleh1s.subscriptionsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SubscriptionsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SubscriptionsServiceApplication.class, args);
	}

}
