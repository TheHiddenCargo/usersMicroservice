package arsw.tamaltolimense.playermanager.controller;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.Bid;
import arsw.tamaltolimense.playermanager.service.UserService;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users/")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {this.userService = userService;}

    @GetMapping("/{nickName}/balance")
    public ResponseEntity<Object> getUserBalance(@PathVariable("nickName") String nickName) {
        try{
            return  new ResponseEntity<>(userService.getUserBalance(nickName),HttpStatus.OK);
        }catch(UserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{nickName}/info")
    public ResponseEntity<Object> getUserInfo(@PathVariable("nickName") String nickName) {
        try{
            return new ResponseEntity<>(userService.getUserInfo(nickName),HttpStatus.OK);
        }catch (UserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestParam("email") String email, @RequestParam("nickName") String nickName) {
        try{
            return new ResponseEntity<>(userService.registerUser(email,nickName), HttpStatus.CREATED);
        }catch(UserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch(DuplicateKeyException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/deposit")
    public ResponseEntity<Object> deposit(@RequestParam("nickName") String nickName, @RequestParam("amount") int amount) {
        try{
            userService.deposit(nickName,amount);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(UserException e){
            if(e.getMessage().equals(UserException.USER_NOT_FOUND)) return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/withdraw")
    public ResponseEntity<Object> withdraw(@RequestParam("nickName") String nickName, @RequestParam("amount") int amount) {
        try {
            userService.withdraw(nickName,amount);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (UserException e){
            if(e.getMessage().equals(UserException.USER_NOT_FOUND)) return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            if(e.getMessage().equals(UserException.NEGATIVE_BALANCE)) return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }



    @PutMapping("/bet")
    public ResponseEntity<Object> bet(@RequestParam("nickName") String nickName,@RequestBody Bid bid) {
        try {
            userService.bet(nickName, bid);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserException e) {
            if (e.getMessage().equals(UserException.USER_NOT_FOUND))
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            if (e.getMessage().equals(UserException.NEGATIVE_BALANCE))
                return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{nickName}")
    public ResponseEntity<Object> nickName(@PathVariable("nickName") String nickName,
                                      @RequestParam(value = "newNickName", required = false) String newNickName,
                                      @RequestParam(value = "photo", required = false) String photo) {
        try{
            return new ResponseEntity<>(userService.updateUser(nickName,newNickName,photo),HttpStatus.OK);
        }catch (UserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch(DuplicateKeyException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/delete/{nickName}")
    public ResponseEntity<Object> delete(@PathVariable("nickName") String nickName) {
        userService.deleteUser(nickName);
        return new ResponseEntity<>(HttpStatus.OK);


    }

    @DeleteMapping("/cleanbets/{nickName}")
    public ResponseEntity<Object> cleanBets(@PathVariable("nickName") String nickName) {
        try {
            userService.cleanBids(nickName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


}
