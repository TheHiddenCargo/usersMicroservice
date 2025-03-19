package arsw.tamaltolimense.playermanager.service;

import arsw.tamaltolimense.playermanager.UserException;
import arsw.tamaltolimense.playermanager.model.Bid;
import arsw.tamaltolimense.playermanager.model.User;
import com.mongodb.DuplicateKeyException;


import java.util.List;

public interface UserService {

    User registerUser(String email, String nickName) throws UserException, DuplicateKeyException;

    void registerBid(String userEmail,String container, int amount) throws UserException;

    void registerBid(String userEmail,Bid bid) throws UserException;

    List<User> getUsers();

    User getUser(String email) throws UserException;

    int getUserBalance(String email) throws UserException;

    List<Bid> getBids(String userEmail) throws UserException;

    int deposit(String email, int amount) throws UserException;

    int withdraw(String email, int amount) throws UserException;

    void updateNickName(String email, String newNickName) throws UserException,DuplicateKeyException;

    void updatePhoto(String email, String photo) throws UserException;

    void deleteUser(String email);

    void cleanBids(String userEmail) throws UserException;











}
