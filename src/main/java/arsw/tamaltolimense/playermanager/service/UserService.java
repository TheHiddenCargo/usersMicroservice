package arsw.tamaltolimense.playermanager.service;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.Bid;
import arsw.tamaltolimense.playermanager.model.User;



import java.util.List;

public interface UserService {

    User registerUser(String email, String nickName) throws UserException;

    int getUserBalance(String nickName) throws UserException;

    String[] getUserInfo(String nickName) throws UserException;

    List<Bid> getBids(String nickName) throws UserException;

    void transaction(String nickName, int amount) throws UserException;

    void registerBid(String nickName, Bid bid) throws UserException;

    User updatePhoto(String nickName, String photo) throws UserException;

    User updateNickName(String nickName, String newNickName) throws UserException;

    void deleteUser(String nickName);

    User cleanBids(String nickName) throws UserException;











}
