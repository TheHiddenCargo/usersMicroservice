package arsw.tamaltolimense.playermanager.service;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.User;

import java.util.Map;

public interface UserService {


    User registerUser(String email, String nickName, int balance, String icon) throws UserException;

    /**
     * Get the actual balance of a user
     * @param email, email of the user
     * @return current balance of the user
     * @throws UserException
     */
    int getUserBalance(String email) throws UserException;

    /**
     * Gives the user and photo of the user
     * @param email
     * @return map with the user and icon
     * @throws UserException
     */
    Map<String,String> getUserInfo(String email) throws UserException;

    void transaction(String email, int amount) throws UserException;

    User updatePhoto(String email, String photo) throws UserException;

    User updateNickName(String email, String newNickName) throws UserException;

    void deleteUser(String email);

}
