package longbridge.exception;

/**
 * Created by Fortune on 5/8/2017.
 */
public class InternetBankingException extends RuntimeException {

    public InternetBankingException() {
        super("Failed to perform the requested action");
    }

    public InternetBankingException(Throwable cause) {
        super("Failed to perform the requested action", cause);
    }

    public InternetBankingException(String message) {
        super(message);
    }

    public InternetBankingException(String message, Throwable cause) {
        super(message, cause);
    }
}
