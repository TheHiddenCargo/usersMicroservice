package arsw.tamaltolimense.playermanager.controller;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.User;
import arsw.tamaltolimense.playermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;
    private static final String ERROR = "error";

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{nickName}/info")
    public ResponseEntity<Object> getUserInfo(@PathVariable("nickName") String nickName) {
        try {
            Map<String,String> userInfo = userService.getUserInfo(nickName);
            return ResponseEntity.ok(userInfo);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR, e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody Map<String, String> userData) {
        try {
            String email = userData.get("email");
            String nickName = userData.get("nickName");
            int balance = Integer.parseInt(userData.get("balance"));
            String icon = userData.get("icon");
            User user = userService.registerUser(email, nickName, balance, icon);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (UserException e) {
            HttpStatus status = e.getMessage().equals(UserException.NULL_VALUE) ? HttpStatus.BAD_REQUEST : HttpStatus.CONFLICT;
            return ResponseEntity.status(status).body(Map.of(ERROR, e.getMessage()));
        }
    }

    @PutMapping("/update/nickname/{nickName}")
    public ResponseEntity<Object> updateNickName(@PathVariable("nickName") String nickName, @RequestBody Map<String, String> data) {
        try {
            String newNickName = data.get("newNickName");
            User updatedUser = userService.updateNickName(nickName, newNickName);
            return ResponseEntity.ok(updatedUser);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERROR, e.getMessage()));
        }
    }

    @PutMapping("/update/photo/{nickName}")
    public ResponseEntity<Object> updatePhoto(@PathVariable("nickName") String nickName, @RequestBody Map<String, String> data) {
        try {
            String photo = data.get("photo");
            User updatedUser = userService.updatePhoto(nickName, photo);
            return ResponseEntity.ok(updatedUser);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERROR, e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{nickName}")
    public ResponseEntity<Object> deleteUser(@PathVariable("nickName") String nickName) {
        userService.deleteUser(nickName);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @GetMapping("/email/{email}/info")
    public ResponseEntity<Object> getUserByEmail(@PathVariable("email") String email) {
        try {
            User user = userService.getUserByEmail(email);
            return new ResponseEntity<>(user,HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }
}
