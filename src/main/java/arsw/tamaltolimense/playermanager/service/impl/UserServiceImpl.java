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

    private void notifyBalanceChange(User user){
        messagingTemplate.convertAndSend("/balance/" + user.getNickName(), user.getBalance());
    }

    private void notifyBidChange(User user){
        messagingTemplate.convertAndSend("/balance/" + user.getNickName(), user.getBids());
    }


    @Override
    public User registerUser(String email, String nickName) throws UserException, DuplicateKeyException {
        if(nickName == null || nickName.isEmpty()) throw new UserException(UserException.NULL_VALUE);
        if(email == null || email.isEmpty()) throw new UserException(UserException.NULL_VALUE);
        return userRepository.save(new User(email,nickName));
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
        notifyBalanceChange(userRepository.save(currentUser));
    }

    @Override
    public void withdraw(String nickName, int amount) throws UserException {
        User currentUser = this.getUser(nickName);
        currentUser.transaction(User.WITHDRAW,amount);
        notifyBalanceChange(userRepository.save(currentUser));
    }




    @Override
    public void bet(String nickname, Bid bid) throws UserException {
        if(bid == null) throw new UserException(UserException.NULL_VALUE);
        if(bid.getContainerId() == null || bid.getContainerId().isEmpty()) throw new UserException(UserException.NULL_CONTAINER);
        User user = this.getUser(nickname);
        user.transaction(User.WITHDRAW,bid.getAmount());
        user.registerBet(bid);
        userRepository.save(user);
        notifyBalanceChange(user);
        notifyBidChange(user);
    }

    @Override
    public User updateUser(String nickName, String newNickName, String photo) throws UserException,DuplicateKeyException {
        User currentUser = this.getUser(nickName);
        if(newNickName != null && !newNickName.equals(currentUser.getNickName()) && !newNickName.isEmpty()) currentUser.setNickName(newNickName);
        if(photo != null && !photo.equals(currentUser.getImagePath())) currentUser.setImagePath(photo);
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
