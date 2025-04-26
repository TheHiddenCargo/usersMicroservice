package arsw.tamaltolimense.playermanager.service.impl;

import arsw.tamaltolimense.playermanager.exception.UserException;
import arsw.tamaltolimense.playermanager.model.User;
import arsw.tamaltolimense.playermanager.repository.UserRepository;
import arsw.tamaltolimense.playermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public String getEmailByUsername(String username) throws UserException {
        // Get all users from your repository
        List<User> allUsers = userRepository.findAll();
        
        // Find the user with the matching username
        // This assumes your User entity has a getNickname() or getUsername() method
        // Adjust the method name according to your actual User entity
        return allUsers.stream()
                .filter(user -> username.equals(user.getNickName()))
                .map(User::getEmail)
                .findFirst()
                .orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));
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

    @Override
    public int getUserBalance(String email) throws UserException {
        return this.getUser(email).getBalance();
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

    private User getUser(String email) throws UserException {
        User user = userRepository.findByEmail(email);
        if(user == null) throw new UserException(UserException.USER_NOT_FOUND);
        return user;
    }

    @Override
    public Map<String,String> getUserInfo(String email) throws UserException {
        User currentUser = getUser(email);
        Map<String,String> userInfo = new HashMap<>();
        userInfo.put("nickname", currentUser.getNickName());
        userInfo.put("photo", currentUser.getImagePath());
        return userInfo;
    }

    /**
     * Realiza una transacción directamente usando el nickname del usuario
     *
     * @param nickname El nickname del usuario
     * @param amount La cantidad a ajustar en el balance (positiva o negativa)
     * @throws UserException Si el usuario no existe o si el balance resultante sería negativo
     */
    @Override
    public void transactionByNickname(String nickname, int amount) throws UserException {
        // Buscar usuario por nickname
        User user = userRepository.findAll().stream()
                .filter(u -> nickname.equals(u.getNickName()))
                .findFirst()
                .orElseThrow(() -> new UserException(UserException.USER_NOT_FOUND));

        // Verificar que el balance resultante no sea negativo
        int balance = user.getBalance();
        if(balance + amount < 0) throw new UserException(UserException.NEGATIVE_BALANCE);

        // Realizar la transacción
        user.transaction(amount);
        userRepository.save(user);
    }

    /**
     * Obtiene el balance de un usuario usando su nickname
     *
     * @param nickname El nickname del usuario
     * @return El balance actual del usuario
     * @throws UserException Si el usuario no existe
     */
    @Override
    public int getUserBalanceByNickname(String nickname) throws UserException {
        return this.getUser(nickname).getBalance();
    }



    @Override
    public void transaction(String email, int amount) throws UserException {
        User currentUser = this.getUser(email);
        int balance = currentUser.getBalance();
        if(balance + amount < 0) throw new UserException(UserException.NEGATIVE_BALANCE);
        currentUser.transaction(amount);
        userRepository.save(currentUser);
    }

    @Override
    public User updateNickName(String email, String newNickName) throws UserException {
        String currentNickName = getUserInfo(email).get("nickname");

        if(newNickName == null || newNickName.trim().equals("")) throw new UserException(UserException.NULL_NICK_NAME);
        if(!currentNickName.equals(newNickName) && checkNickName(newNickName)) throw new UserException(UserException.NICK_NAME_FOUND);
        User currentUser = this.getUser(email);
        currentUser.setNickName(newNickName);
        return userRepository.save(currentUser);
    }

    @Override
    public User updatePhoto(String email, String photo) throws UserException{
        if(photo == null || photo.trim().equals("")) throw new UserException(UserException.NULL_VALUE);
        User user = this.getUser(email);
        if(!photo.equals(getUserInfo(email).get("photo"))){
            user.setImagePath(photo);
            user = userRepository.save(user);
        }
        return user;

    }

    @Override
    public void deleteUser(String email){
        userRepository.deleteByEmail(email);
    }
}
