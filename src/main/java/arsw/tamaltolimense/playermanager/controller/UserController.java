package arsw.tamaltolimense.playermanager.controller;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.service.UserService;
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
        }catch(UserException e) {
            if(e.getMessage().equals(UserException.NULL_VALUE)) return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
    /**
    @PutMapping("/bids")
    public ResponseEntity<Object> registerBid(@RequestParam("nickname") String nickName, @RequestBody Bid bid){
        try{
            userService.registerBid(nickName,bid);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }catch(UserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
     **/




    @PutMapping("/update/nickname/{nickName}")
    public ResponseEntity<Object> nickName(@PathVariable("nickName") String nickName,
                                      @RequestParam(value = "newNickName") String newNickName) {
        try{
            return new ResponseEntity<>(userService.updateNickName(nickName,newNickName),HttpStatus.OK);
        }catch (UserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/photo/{nickName}")
    public ResponseEntity<Object> photo(@PathVariable("nickName") String nickName,
                                           @RequestParam(value = "photo") String photo) {
        try{
            return new ResponseEntity<>(userService.updatePhoto(nickName,photo),HttpStatus.OK);
        }catch (UserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{nickName}")
    public ResponseEntity<Object> delete(@PathVariable("nickName") String nickName) {
        userService.deleteUser(nickName);
        return new ResponseEntity<>(HttpStatus.OK);


    }




}
