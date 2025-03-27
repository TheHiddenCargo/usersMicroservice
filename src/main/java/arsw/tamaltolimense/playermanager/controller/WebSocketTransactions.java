package arsw.tamaltolimense.playermanager.controller;
import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.handler.annotation.SendTo;

import org.springframework.stereotype.Controller;


@Controller
public class WebSocketTransactions {


    private UserService userService;

    @Autowired
    public WebSocketTransactions(UserService userService) {
        this.userService = userService;
    }

    @MessageMapping("/user/{nickName}")
    @SendTo("transactions/made/{nickName}")
    public int madeTransaction(@Payload int transaction,@DestinationVariable String nickName){
        try{
            userService.transaction(nickName,transaction);
            return userService.getUserBalance(nickName);
        }catch (UserException e) {
            return 0;
        }
    }
    @MessageMapping("/balance/{nickName}")
    @SendTo("transactions/made/{nickName}")
    public int getBalance(@DestinationVariable String nickName){
        try{
            return userService.getUserBalance(nickName);
        }catch (UserException e) {
            return 0;
        }
    }
}