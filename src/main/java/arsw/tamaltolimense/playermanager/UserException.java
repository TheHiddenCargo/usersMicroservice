package arsw.tamaltolimense.playermanager;

public class UserException extends Exception {
    public static final String NEGATIVE_BALANCE = "Balance can't be negative";
    public static final String NEGATIVE_VALUE = "Value can't be negative";
    public static final String NULL_VALUE = "Value can't be null";
    public static final String INVALID_TRANSACTION = "Invalid transaction";
    public static final String USER_NOT_FOUND = "User does not exist";
    public UserException(String message) {
        super(message);
    }
}
