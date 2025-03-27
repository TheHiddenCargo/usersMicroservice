package arsw.tamaltolimense.playermanager.service.impl;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.User;
import arsw.tamaltolimense.playermanager.repository.UserRepository;
import arsw.tamaltolimense.playermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByEmail(String email) throws UserException {
        // O, si usaste findByEmail, podrías hacerlo de la siguiente forma:
        User user = userRepository.findByEmail(email);
        if(user == null) throw new UserException(UserException.USER_NOT_FOUND);
        return user;
    }

    @Override
    public User registerUser(String email, String nickName, int balance, String icon) throws UserException {
        if(nickName == null || nickName.trim().equals(""))
            throw new UserException(UserException.NULL_VALUE);
        if(email == null || email.trim().equals(""))
            throw new UserException(UserException.NULL_VALUE);
        if(icon == null || icon.trim().equals("")) throw new UserException(UserException.NULL_VALUE);
        if(balance < 0) throw new UserException(UserException.NEGATIVE_BALANCE);
        if(checkNickName(nickName))
            throw new UserException(UserException.NICK_NAME_FOUND);
        if(checkEmail(email))
            throw new UserException(UserException.EMAIL_FOUND);
        return userRepository.save(new User(email, nickName,balance,icon));
    }

    private boolean checkNickName(String nickName){
        for(User user : userRepository.findAll()){
            if(user.getNickName().equals(nickName))
                return true;
        }
        return false;
    }

    private boolean checkEmail(String email){
        for(User user : userRepository.findAll()){
            if(user.getEmail().equals(email))
                return true;
        }
        return false;
    }

    private User getUser(String nickName) throws UserException {
        User currentUser = userRepository.findByNickName(nickName.toLowerCase());
        if(currentUser == null)
            throw new UserException(UserException.USER_NOT_FOUND);
        return currentUser;
    }

    @Override
    public Map<String,String> getUserInfo(String nickName) throws UserException {
        User currentUser = getUser(nickName);
        Map<String,String> userInfo = new HashMap<>();
        userInfo.put("nickName", currentUser.getNickName());
        userInfo.put("photo", currentUser.getImagePath());
        return userInfo;
    }

    @Override
    public int getUserBalance(String nickName) throws UserException {
        return this.getUser(nickName).getBalance();
    }

    @Override
    public void transaction(String nickName, int amount) throws UserException {
        User currentUser = this.getUser(nickName);
        currentUser.transaction(amount);
        userRepository.save(currentUser);
    }

    @Override
    public User updateNickName(String nickName, String newNickName) throws UserException {
        if(newNickName == null || newNickName.trim().equals(""))
            throw new UserException(UserException.NULL_NICK_NAME);
        if(!nickName.equals(newNickName) && checkNickName(newNickName))
            throw new UserException(UserException.NICK_NAME_FOUND);
        User currentUser = this.getUser(nickName);
        currentUser.setNickName(newNickName);
        return userRepository.save(currentUser);
    }

    @Override
    public User updatePhoto(String nickname, String photo) throws UserException{
        if(photo == null || photo.trim().equals(""))
            throw new UserException(UserException.NULL_VALUE);
        User user = this.getUser(nickname);
        if(!photo.equals(user.getImagePath()))
            user.setImagePath(photo);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String nickName){
        userRepository.deleteByNickName(nickName);
    }
}
