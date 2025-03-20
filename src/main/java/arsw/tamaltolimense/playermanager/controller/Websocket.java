package arsw.tamaltolimense.playermanager.controller;
import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.Bid;
import arsw.tamaltolimense.playermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class Websocket {


    private UserService userService;


    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public Websocket(UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/balance.get")
    public void getBalance(@Payload String email) throws UserException {
        int balance = userService.getUserBalance(email);
        messagingTemplate.convertAndSend("/balance/" + email, balance);
    }

    @MessageMapping("/bids.get")
    public void getBids(@Payload String email) throws UserException {
        List<Bid> bids = userService.getBids(email);
        messagingTemplate.convertAndSend("/bids/" + email, bids);
    }
}
