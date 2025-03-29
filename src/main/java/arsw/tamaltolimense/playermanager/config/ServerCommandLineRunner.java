package arsw.tamaltolimense.playermanager.config;

import java.util.HashMap;
import java.util.Map;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;

import com.corundumstudio.socketio.listener.DisconnectListener;


/**
 * Log for connections on the Socket IO server
 */
@Component
public class ServerCommandLineRunner implements CommandLineRunner {

    private final SocketIOServer server;
    private UserService userService;
    private static final String SENT_INFO = "sent_info";
    private static final String NICKNAME = "nickname";
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

        server.addConnectListener(client -> {
            String user = client.getHandshakeData().getSingleUrlParam(EMAIL);
            client.joinRoom(user);
            System.out.println("Cliente conectado: " + client.getSessionId() + ", sala: " + user);

        });

        server.addEventListener(SENT_INFO,Map.class,(client,data,ackRequest) ->{
            String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
            String nickname = (String) data.get(NICKNAME);
            System.out.println("Informacion enviada: " + nickname + data);
            server.getRoomOperations(email).sendEvent("get_info", userService.getUserInfo(nickname));
        });

        server.addEventListener("update_photo",Map.class,(client,data,ackRequest) ->{
            String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
            String nickname = (String) data.get(NICKNAME);
            String newPhoto = (String) data.get("newPhoto");
            userService.updatePhoto(nickname, newPhoto);
            Map<String,String> message = new HashMap<>();
            message.put(NICKNAME, nickname);
            server.getRoomOperations(email).sendEvent(SENT_INFO, message);
        });

        server.addEventListener("update_nickname",Map.class,(client,data,ackRequest) ->{
            Map<String,String> message = new HashMap<>();
            String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
            String nickname = (String) data.get(NICKNAME);
            String newNickname = (String) data.get("newNickname");
            try{
                userService.updateNickName(nickname,newNickname);
                message.put(NICKNAME, newNickname);
            }catch (UserException e) {
                message.put(NICKNAME, nickname);
            }
            server.getRoomOperations(email).sendEvent(SENT_INFO, message);
        });

        server.addEventListener("sent_balance",Map.class,(client,data,ackRequest) ->{
            String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
            String nickname = (String) data.get(NICKNAME);
            int actualBalance = userService.getUserBalance(nickname);
            Map<String, Object> dataBalance = new HashMap<>();
            dataBalance.put("userBalance", actualBalance);
            System.out.println("Balance enviada: " + nickname + data);
            server.getRoomOperations(email).sendEvent("accept_balance", dataBalance);
        });

        server.addEventListener("offer",Map.class,(client,data,ackRequest) ->{
            String email = client.getHandshakeData().getSingleUrlParam(EMAIL);
            String nickname = (String) data.get(NICKNAME);
            int amount = (int) data.get("amount");
            userService.transaction(nickname,amount);
            Map<String, Object> dataBalance = new HashMap<>();
            dataBalance.put("balance", userService.getUserBalance(nickname));
            server.getRoomOperations(email).sendEvent("accept_balance", dataBalance);
        });


        server.addDisconnectListener(client -> System.out.println("Cliente desconectado: " + client.getSessionId()));

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

    }
}

