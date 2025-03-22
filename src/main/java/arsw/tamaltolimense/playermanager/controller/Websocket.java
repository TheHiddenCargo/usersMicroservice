package arsw.tamaltolimense.playermanager.controller;
import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class    Websocket {


    private UserService userService;


    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public Websocket(UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/getbalance")
    public void getBalance(@Payload String nickname) {
        try{
            messagingTemplate.convertAndSend("/transactions/made/balance/" + nickname, userService.getUserBalance(nickname));
        }catch (UserException e) {
            messagingTemplate.convertAndSend("/transactions/made/balance/" + nickname, "User does not exist");
        }

    }

    @MessageMapping("/getbids")
    public void getBids(@Payload String nickname) {
        try{
            messagingTemplate.convertAndSend("/transactions/made/bids/" + nickname, userService.getBids(nickname));
        }catch (UserException e) {
            messagingTemplate.convertAndSend("/transactions/made/bids/" + nickname, "User does not exist");
        }
    }
}
