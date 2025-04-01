package arsw.tamaltolimense.playermanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.corundumstudio.socketio.SocketIOServer;



/**
 * Configuration for Socket IO
 */
@Configuration
public class SocketIOConfig {

    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private Integer port;

    @Value("${socket-server.origin}")
    private String origin;
    /**
     * Returns a Socket IO Server
     */
    @Bean
    public SocketIOServer socketIOServer() {

        System.out.println("SocketIOServer" + host + ":" + port + origin);
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("0.0.0.0");
        config.setPort(port);
        config.setOrigin(origin);

        // Configuraciones para compatibilidad con Socket.IO v3
        config.setAllowCustomRequests(true);
        config.setUpgradeTimeout(10000);
        config.setPingTimeout(60000);
        config.setPingInterval(25000);

        return new SocketIOServer(config);
    }

}