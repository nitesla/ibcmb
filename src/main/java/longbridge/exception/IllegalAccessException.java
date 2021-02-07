package longbridge.exception;

public class IllegalAccessException extends InternetBankingException{
    public IllegalAccessException() {
        super("Unknown ID");
    }

    public IllegalAccessException(Throwable cause) {
        super(cause);
    }

    public IllegalAccessException(String message) {
        super(message);
    }

    public IllegalAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
