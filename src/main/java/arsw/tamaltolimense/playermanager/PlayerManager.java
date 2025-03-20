package arsw.tamaltolimense.playermanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class PlayerManager {

	public static void main(String[] args) {
		if (System.getenv("DB_URI") == null) { // Verifica si la variable ya está en el entorno
			try {
				Dotenv dotenv = Dotenv.load(); // Intenta cargar el archivo .env
				System.setProperty("DB_URI", dotenv.get("DB_URI"));
			} catch (Exception e) {
				System.out.println("No .env file found, using system environment variables.");
			}
		}
		SpringApplication.run(PlayerManager.class, args);
	}


}
