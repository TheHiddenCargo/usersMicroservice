package arsw.tamaltolimense.playermanager.service.impl;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.Bid;
import arsw.tamaltolimense.playermanager.model.User;
import arsw.tamaltolimense.playermanager.repository.UserRepository;
import arsw.tamaltolimense.playermanager.service.UserService;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    private UserRepository userRepository;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    private void notifyBalanceChange(String nickName) throws UserException {
        messagingTemplate.convertAndSend("/balance/" + nickName, this.getUserBalance(nickName));
    }

    private void notifyBidChange(String nickName, List<Bid> bids){
        messagingTemplate.convertAndSend("/balance/" + nickName, bids);
    }


    @Override
    public User registerUser(String email, String nickName) throws UserException, DuplicateKeyException {
        try{
            return this.getUser(nickName);
        }catch (UserException e){
            return userRepository.save(new User(nickName,nickName));
        }
    }


    private User getUser(String nickName) throws  UserException{
        User currentUser = userRepository.findByNickName(nickName.toLowerCase());
        if(currentUser == null) throw new UserException(UserException.USER_NOT_FOUND);
        return currentUser;
    }

    @Override
    public String[] getUserInfo(String nickName) throws UserException {
        User user = this.getUser(nickName);
        return new String[]{user.getNickName(), user.getImagePath()};
    }

    @Override
    public int getUserBalance(String nickName) throws UserException{
        return this.getUser(nickName).getBalance();
    }

    @Override
    public List<Bid> getBids(String nickName) throws UserException{
        return this.getUser(nickName).getBids();
    }

    @Override
    public void deposit(String nickName, int amount) throws UserException {
        User currentUser = this.getUser(nickName);
        currentUser.transaction(User.DEPOSIT,amount);
        userRepository.save(currentUser);
        notifyBalanceChange(nickName);

    }

    @Override
    public void withdraw(String nickName, int amount) throws UserException {
        User currentUser = this.getUser(nickName);
        currentUser.transaction(User.WITHDRAW,amount);
        userRepository.save(currentUser);
        notifyBalanceChange(nickName);

    }

    @Override
    public void bet(String nickName, int amount, String container) throws UserException{
        User currentUser = this.getUser(nickName);
        this.registerBet(currentUser,new Bid(container,amount));
    }

    @Override
    public void bet(String nickName, Bid bid) throws UserException{
        User currentUser = this.getUser(nickName);
        this.registerBet(currentUser,bid);
    }


    private void registerBet(User user, Bid bid) throws UserException {
        if(bid.getAmount() <= 0) throw new UserException(UserException.NEGATIVE_VALUE);
        user.registerBet(bid);
        userRepository.save(user);
        notifyBalanceChange(user.getNickName());
        notifyBidChange(user.getNickName(),user.getBids());
    }

    @Override
    public User updateUser(String nickName, String newNickName, String photo) throws UserException,DuplicateKeyException {
        User currentUser = this.getUser(nickName);
        if(!newNickName.equals(currentUser.getNickName())) currentUser.setNickName(newNickName);
        if(!photo.equals(currentUser.getImagePath())) currentUser.setImagePath(photo);
        return userRepository.save(currentUser);
    }



    @Override
    public void deleteUser(String nickName){
        userRepository.deleteByNickName(nickName);
    }

    @Override
    public void cleanBids(String nickName) throws UserException{
        User currentUser = this.getUser(nickName);
        currentUser.cleanBids();
        userRepository.save(currentUser);
    }

}
