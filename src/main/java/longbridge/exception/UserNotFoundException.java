package longbridge.exception;

/**
 * Created by Fortune on 5/8/2017.
 */
public class UserNotFoundException extends InternetBankingException {

    public UserNotFoundException() {
        super("User not found");
    }

    public UserNotFoundException(Throwable cause) {
        super("User not found", cause);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
