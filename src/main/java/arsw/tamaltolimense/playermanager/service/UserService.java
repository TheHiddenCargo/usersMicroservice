package arsw.tamaltolimense.playermanager.service;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.Bid;
import arsw.tamaltolimense.playermanager.model.User;
import com.mongodb.DuplicateKeyException;


import java.util.List;

public interface UserService {

    User registerUser(String email, String nickName) throws UserException, DuplicateKeyException;

    int getUserBalance(String nickName) throws UserException;

    String[] getUserInfo(String nickName) throws UserException,DuplicateKeyException;

    List<Bid> getBids(String nickName) throws UserException;

    void deposit(String nickName, int amount) throws UserException;

    void withdraw(String nickName, int amount) throws UserException;


    void bet(String nickName, Bid bid) throws UserException;

    User updateUser(String nickName, String newNickName, String photo) throws UserException,DuplicateKeyException;


    void deleteUser(String nickName);

    void cleanBids(String nickName) throws UserException;











}
