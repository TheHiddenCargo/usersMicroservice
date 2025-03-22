package arsw.tamaltolimense.playermanager.service.impl;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.Bid;
import arsw.tamaltolimense.playermanager.model.User;
import arsw.tamaltolimense.playermanager.repository.UserRepository;
import arsw.tamaltolimense.playermanager.service.UserService;
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
        messagingTemplate.convertAndSend("/transactions/made/balance/" + user.getNickName(), user.getBalance());
    }

    private void notifyBidChange(User user){
        messagingTemplate.convertAndSend("/transactions/made/bids/" + user.getNickName(), user.getBids());
    }


    @Override
    public User registerUser(String email, String nickName) throws UserException {
        if(nickName == null || nickName.trim().equals("")) throw new UserException(UserException.NULL_VALUE);
        if(email == null || email.trim().equals("")) throw new UserException(UserException.NULL_VALUE);
        if(checkNickName(nickName)) throw new UserException(UserException.NICK_NAME_FOUND);
        if(checkEmail(email)) throw new UserException(UserException.EMAIL_FOUND);
        return userRepository.save(new User(email,nickName));
    }

    private boolean checkNickName(String nickName){
        for(User user : userRepository.findAll()){
            if(user.getNickName().equals(nickName)) return true;
        }
        return false;
    }

    private boolean checkEmail(String email){
        for(User user : userRepository.findAll()){
            if(user.getEmail().equals(email)) return true;
        }
        return false;
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
    public void transaction(String nickName, int amount) throws UserException {
        User currentUser = this.getUser(nickName);
        currentUser.transaction(amount);
        notifyBalanceChange(userRepository.save(currentUser));
    }


    @Override
    public void registerBid(String nickname, Bid bid) throws UserException {
        User user = this.getUser(nickname);
        user.registerBet(bid);
        notifyBidChange(userRepository.save(user));
    }

    @Override
    public User updateNickName(String nickName, String newNickName) throws UserException {
        if(newNickName == null || newNickName.trim().equals("")) throw new UserException(UserException.NULL_NICK_NAME);
        if(!nickName.equals(newNickName) && checkNickName(newNickName)) throw new UserException(UserException.NICK_NAME_FOUND);
        User currentUser = this.getUser(nickName);
        currentUser.setNickName(newNickName);
        return userRepository.save(currentUser);
    }

    @Override
    public User updatePhoto(String nickname, String photo) throws UserException{
        if(photo == null) throw new UserException(UserException.NULL_VALUE);
        User user = this.getUser(nickname);
        if(!photo.equals(user.getImagePath())) user.setImagePath(photo);

        return userRepository.save(user);
    }



    @Override
    public void deleteUser(String nickName){
        userRepository.deleteByNickName(nickName);
    }

    @Override
    public User cleanBids(String nickName) throws UserException{
        User currentUser = this.getUser(nickName);
        currentUser.cleanBids();
        return (userRepository.save(currentUser));
    }

}
