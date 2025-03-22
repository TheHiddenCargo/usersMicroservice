package arsw.tamaltolimense.playermanager.exception;

public class UserException extends Exception {
    public static final String NULL_VALUE = "Value can't be null";
    public static final String USER_NOT_FOUND = "User does not exist";
    public static final String NICK_NAME_FOUND = "Nickname already exist";
    public static final String EMAIL_FOUND = "Email already exist";
    public static final String NULL_NICK_NAME= "Nickname can't be null";
    public UserException(String message) {
        super(message);
    }
}
