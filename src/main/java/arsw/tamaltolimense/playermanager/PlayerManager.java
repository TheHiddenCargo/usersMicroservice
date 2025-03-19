package arsw.tamaltolimense.playermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class PlayerManager {

	public static void main(String[] args) {

		SpringApplication.run(PlayerManager.class, args);
	}

}
