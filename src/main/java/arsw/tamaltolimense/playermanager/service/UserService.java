package arsw.tamaltolimense.playermanager.service;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.User;

public interface UserService {

    User registerUser(String email, String nickName,int balance, String icon) throws UserException;

    int getUserBalance(String nickName) throws UserException;

    // Cambiamos para que retorne User en lugar de String[]
    User getUserInfo(String nickName) throws UserException;

    void transaction(String nickName, int amount) throws UserException;

    User updatePhoto(String nickName, String photo) throws UserException;

    User updateNickName(String nickName, String newNickName) throws UserException;

    void deleteUser(String nickName);

    User getUserByEmail(String email) throws UserException;
}
