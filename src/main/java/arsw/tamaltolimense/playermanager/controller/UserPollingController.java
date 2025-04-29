package arsw.tamaltolimense.playermanager.controller;


import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("polling/users")
@CrossOrigin(origins = "*")
public class UserPollingController {

    private final UserService userService;

    private static final String ERROR = "error";

    private static final String USER_BALANCE = "userBalance";

    private static final String BALANCE = "_balance";

    private static final String INFO = "_info";
    
    private static final String EMAIL = "email";

    private final Map<String,Long> lastUpdatedTimeStamps = new ConcurrentHashMap<>();

    @Value("${app.polling.timeout:30000}")
    private long pollingTimeout;

    @Value("${app.polling.check-interval:1000}")
    private long checkInterval;

    @Autowired
    public UserPollingController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/{email}/info")
    public ResponseEntity<Object> pollUserInfo(@PathVariable String email, @RequestParam(required = false) Long timestamp) {
        try {
            if (timestamp == null) {
                // Primera solicitud, devuelve los datos inmediatamente
                Map<String, String> userInfo = userService.getUserInfo(email);
                lastUpdatedTimeStamps.put(email + INFO, System.currentTimeMillis());
                return ResponseEntity.ok(userInfo);
            }

            // Comprueba si hay datos nuevos
            long storedTimestamp = lastUpdatedTimeStamps.getOrDefault(email + INFO, 0L);

            // Si el timestamp del cliente es más antiguo, hay datos nuevos
            if (timestamp < storedTimestamp) {
                Map<String, String> userInfo = userService.getUserInfo(email);
                return ResponseEntity.ok(userInfo);
            }

            // Implementación de long polling (espera hasta 30 segundos)
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < pollingTimeout) { // 30 segundos máximo
                Thread.sleep(checkInterval); // Comprueba cada segundo

                storedTimestamp = lastUpdatedTimeStamps.getOrDefault(email + INFO, 0L);
                if (timestamp < storedTimestamp) {
                    Map<String, String> userInfo = userService.getUserInfo(email);
                    return ResponseEntity.ok(userInfo);
                }
            }

            // Timeout - no hay cambios, devuelve 304 Not Modified
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();

        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR, e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(ERROR, "Polling interrupted"));
        }
    }

    @PostMapping("/{email}/balance")
    public ResponseEntity<Object> pollUserBalance(@PathVariable String email, @RequestParam(required = false) Long timestamp) {
        try {
            if (timestamp == null) {

                int balance = userService.getUserBalance(email);
                Map<String, Object> responseData = new HashMap<>();
                responseData.put(USER_BALANCE, balance);
                lastUpdatedTimeStamps.put(email + BALANCE, System.currentTimeMillis());
                return ResponseEntity.ok(responseData);
            }


            long storedTimestamp = lastUpdatedTimeStamps.getOrDefault(email + BALANCE, 0L);

            if (timestamp < storedTimestamp) {
                int balance = userService.getUserBalance(email);
                Map<String, Object> responseData = new HashMap<>();
                responseData.put(USER_BALANCE, balance);
                return ResponseEntity.ok(responseData);
            }

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < pollingTimeout) {
                Thread.sleep(checkInterval);

                storedTimestamp = lastUpdatedTimeStamps.getOrDefault(email + BALANCE, 0L);
                if (timestamp < storedTimestamp) {
                    int balance = userService.getUserBalance(email);
                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put(USER_BALANCE, balance);
                    return ResponseEntity.ok(responseData);
                }
            }

            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();

        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR, e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(ERROR, "Polling interrupted"));
        }
    }

    @PostMapping("/nickname/{nickname}/balance")
    public ResponseEntity<Object> pollUserBalanceByNickname(@PathVariable String nickname, @RequestParam(required = false) Long timestamp) {
        try {
            // Primero obtenemos el email relacionado con el nickname
            String email = userService.getEmailByUsername(nickname);

            if (timestamp == null) {
                // Primera solicitud, devuelve los datos inmediatamente
                int balance = userService.getUserBalanceByNickname(nickname);
                Map<String, Object> responseData = new HashMap<>();
                responseData.put(USER_BALANCE, balance);
                lastUpdatedTimeStamps.put(email + BALANCE, System.currentTimeMillis());
                return ResponseEntity.ok(responseData);
            }

            // Comprueba si hay datos nuevos
            long storedTimestamp = lastUpdatedTimeStamps.getOrDefault(email + BALANCE, 0L);

            // Si el timestamp del cliente es más antiguo, hay datos nuevos
            if (timestamp < storedTimestamp) {
                int balance = userService.getUserBalanceByNickname(nickname);
                Map<String, Object> responseData = new HashMap<>();
                responseData.put(USER_BALANCE, balance);
                return ResponseEntity.ok(responseData);
            }

            // Implementación de long polling (espera hasta 30 segundos)
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < pollingTimeout) { // 30 segundos máximo
                Thread.sleep(checkInterval); // Comprueba cada segundo

                storedTimestamp = lastUpdatedTimeStamps.getOrDefault(email + BALANCE, 0L);
                if (timestamp < storedTimestamp) {
                    int balance = userService.getUserBalanceByNickname(nickname);
                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put(USER_BALANCE, balance);
                    return ResponseEntity.ok(responseData);
                }
            }

            // Timeout - no hay cambios, devuelve 304 Not Modified
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();

        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR, e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(ERROR, "Polling interrupted"));
        }
    }

    @PostMapping("/update/photo")
    public ResponseEntity<Object> updatePhoto(@RequestBody Map<String, String> userData) {
        try {
            String email = userData.get(EMAIL);
            String newPhoto = userData.get("photo");
            userService.updatePhoto(email, newPhoto);
            // Actualiza el timestamp para notificar cambios
            lastUpdatedTimeStamps.put(email + INFO, System.currentTimeMillis());
            return ResponseEntity.ok(userService.getUserInfo(email));
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERROR, e.getMessage()));
        }
    }

    @PostMapping("/update/nickname")
    public ResponseEntity<Object> updateNickname(@RequestBody Map<String, String> userData) {
        try {
            String email = userData.get(EMAIL);
            String newNickname = userData.get("newNickname");
            userService.updateNickName(email, newNickname);

            lastUpdatedTimeStamps.put(email + INFO, System.currentTimeMillis());
            return ResponseEntity.ok(userService.getUserInfo(email));
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERROR, e.getMessage()));
        }
    }

    @PostMapping("/offer")
    public ResponseEntity<Object> makeTransaction(@RequestBody Map<String, Object> userData) {
        try {
            String email = (String) userData.get(EMAIL);
            int amount = (Integer) userData.get("amount");

            userService.transaction(email, amount);
            // Actualiza el timestamp para notificar cambios
            lastUpdatedTimeStamps.put(email + BALANCE, System.currentTimeMillis());

            int balance = userService.getUserBalance(email);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put(USER_BALANCE, balance);
            return ResponseEntity.ok(responseData);
        } catch (UserException e) {
            if(e.getMessage().equals(UserException.NEGATIVE_BALANCE)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(ERROR, e.getMessage()));
            } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERROR, e.getMessage()));

        }
    }
    @PostMapping("/offer/username")
    public ResponseEntity<Object> makeTransactionByUsername(@RequestBody Map<String, Object> userData) {
        try {
            String username = (String) userData.get("username");
            int amount = (Integer) userData.get("amount");

            // Necesitamos implementar estos dos métodos en UserService
            // para trabajar directamente con el nickname
            userService.transactionByNickname(username, amount);

            // Actualizamos el timestamp para notificar cambios
            // Primero obtenemos el email asociado al nickname para mantener la estructura existente
            String email = userService.getEmailByUsername(username);
            lastUpdatedTimeStamps.put(email + BALANCE, System.currentTimeMillis());

            // Obtenemos el balance directamente usando el nickname
            int balance = userService.getUserBalanceByNickname(username);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put(USER_BALANCE, balance);
            return ResponseEntity.ok(responseData);
        } catch (UserException e) {
            if(e.getMessage().equals(UserException.NEGATIVE_BALANCE)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(ERROR, e.getMessage()));
            } else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(ERROR, e.getMessage()));
        }
    }

}
