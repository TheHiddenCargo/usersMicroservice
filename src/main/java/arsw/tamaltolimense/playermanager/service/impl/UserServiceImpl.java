package arsw.tamaltolimense.playermanager.service.impl;

import arsw.tamaltolimense.playermanager.UserException;
import arsw.tamaltolimense.playermanager.model.Bid;
import arsw.tamaltolimense.playermanager.model.User;
import arsw.tamaltolimense.playermanager.repository.UserRepository;
import arsw.tamaltolimense.playermanager.service.UserService;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;




    @Override
    public User registerUser(String email, String nickName) throws UserException, DuplicateKeyException {
        User currentUser = this.getUser(email);
        currentUser.setNickName(nickName);
        return userRepository.save(currentUser);
    }

    @Override
    public void registerBid(String userEmail,String container, int amount) throws UserException{
       User currentUser = this.getUser(userEmail);
       currentUser.registerBid(container, amount);
       userRepository.save(currentUser);

    }
    @Override
    public void registerBid(String userEmail,Bid bid) throws UserException{
        User user = this.getUser(userEmail);
        user.registerBid(bid);
        userRepository.save(user);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public User getUser(String email) throws  UserException{
        User currentUser = userRepository.findByEmail(email.toLowerCase());
        if(currentUser == null) throw new UserException(UserException.USER_NOT_FOUND);
        return currentUser;
    }

    @Override
    public int getUserBalance(String email) throws UserException{
        return this.getUser(email).getBalance();
    }

    @Override
    public List<Bid> getBids(String userEmail) throws UserException{
        return this.getUser(userEmail).getBids();
    }

    @Override
    public int deposit(String email, int amount) throws UserException {
        User currentUser = this.getUser(email);
        currentUser.transaction("deposit", amount);
        userRepository.save(currentUser);
        return currentUser.getBalance();
    }

    @Override
    public int withdraw(String email, int amount) throws UserException {
        User currentUser = this.getUser(email);
        currentUser.transaction("withdraw", amount);
        userRepository.save(currentUser);
        return currentUser.getBalance();
    }

    @Override
    public void updateNickName(String email, String newNickName) throws UserException,DuplicateKeyException {
        User currentUser = this.getUser(email);
        currentUser.setNickName(newNickName);
        userRepository.save(currentUser);

    }

    @Override
    public void updatePhoto(String email, String photo) throws UserException{
        User currentUser = this.getUser(email);
        currentUser.setImagePath(photo);
        userRepository.save(currentUser);
    }

    @Override
    public void deleteUser(String email){
        userRepository.deleteByEmail(email);
    }

    @Override
    public void cleanBids(String userEmail) throws UserException{
        User currentUser = this.getUser(userEmail);
        currentUser.cleanBids();
        userRepository.save(currentUser);
    }


}
