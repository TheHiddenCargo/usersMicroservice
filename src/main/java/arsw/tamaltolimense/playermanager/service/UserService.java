package arsw.tamaltolimense.playermanager.service;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.User;



public interface UserService {

    User registerUser(String email, String nickName) throws UserException;

    int getUserBalance(String nickName) throws UserException;

    String[] getUserInfo(String nickName) throws UserException;


    void transaction(String nickName, int amount) throws UserException;

    User updatePhoto(String nickName, String photo) throws UserException;

    User updateNickName(String nickName, String newNickName) throws UserException;

    void deleteUser(String nickName);













}
