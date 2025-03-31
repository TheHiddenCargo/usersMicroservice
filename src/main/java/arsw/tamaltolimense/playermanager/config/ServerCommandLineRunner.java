package arsw.tamaltolimense.playermanager.config;

import java.util.HashMap;
import java.util.Map;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.service.UserService;
import com.corundumstudio.socketio.listener.ConnectListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import com.corundumstudio.socketio.listener.DisconnectListener;

/**
 * Copilot
 *
 * La clase CommandLineRunner en Spring Boot se utiliza
 * para ejecutar código después de que la aplicación se haya iniciado.
 * Es especialmente útil para tareas que deben ejecutarse una vez que la aplicación está completamente configurada,
 * como iniciar servicios, realizar configuraciones adicionales, o ejecutar scripts de inicialización.
 */

/**
 * Log for connections on the Socket IO server
 */
@Component
public class ServerCommandLineRunner implements CommandLineRunner {

    private final SocketIOServer server;
    private UserService userService;
    private static final String GET_INFO = "get_info";
    private static final String EMAIL = "email";

    /**
     * Runs the Socket IO server
     * @param server
     */
    @Autowired
    public ServerCommandLineRunner(SocketIOServer server, UserService userService) {
        this.server = server;
        this.userService = userService;
    }

    /**
     * Main function
     */
    @Override
    public void run(String... args) throws Exception {
        server.start();
        System.out.println("Servidor Socket.IO iniciado en el puerto " + server.getConfiguration().getPort());

        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("Cliente conectado");
                String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
                client.joinRoom(email);
                client.sendEvent("conexion_confirmada", "Conexión exitosa, bienvenido " + email);

            }
        });

        server.addEventListener("sent_info",Map.class,(client,data,ackRequest) ->{
            String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
            server.getRoomOperations(email).sendEvent(GET_INFO, userService.getUserInfo(email));
            if (ackRequest.isAckRequested()) ackRequest.sendAckData("Solicitud de informacion procesada");
        });

        server.addEventListener("update_photo",Map.class,(client,data,ackRequest) ->{
            String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
            String newPhoto = (String) data.get("newPhoto");
            userService.updatePhoto(email, newPhoto);
            server.getRoomOperations(email).sendEvent(GET_INFO,userService.getUserInfo(email));
            if (ackRequest.isAckRequested()) ackRequest.sendAckData("Actualizacion de icono procesada");
        });

        server.addEventListener("update_nickname",Map.class,(client,data,ackRequest) ->{
            String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
            String newNickname = (String) data.get("newNickname");
            userService.updateNickName(email,newNickname);
            server.getRoomOperations(email).sendEvent(GET_INFO, userService.getUserInfo(email));
            if (ackRequest.isAckRequested()) ackRequest.sendAckData("Actualizacion de nickname procesada");
        });

        server.addEventListener("sent_balance",Map.class,(client,data,ackRequest) ->{
            String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
            int actualBalance = userService.getUserBalance(email);
            Map<String, Object> dataBalance = new HashMap<>();
            dataBalance.put("userBalance", actualBalance);
            server.getRoomOperations(email).sendEvent("accept_balance", dataBalance);
            if (ackRequest.isAckRequested()) ackRequest.sendAckData("Solicitud de balance procesada");
        });

        server.addEventListener("offer",Map.class,(client,data,ackRequest) ->{
            String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
            int amount = (int) data.get("amount");
            userService.transaction(email,amount);
            Map<String, Integer> dataBalance = new HashMap<>();
            dataBalance.put("userBalance", userService.getUserBalance(email));
            server.getRoomOperations(email).sendEvent("accept_balance", dataBalance);
            if (ackRequest.isAckRequested()) ackRequest.sendAckData("Solicitud de apuesta procesada");
        });


        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                System.out.println("Cliente desconectado: " + client.getSessionId());
            }
        });


        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

    }
}

