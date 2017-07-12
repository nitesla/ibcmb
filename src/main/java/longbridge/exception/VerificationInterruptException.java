package longbridge.exception;

/**
 * Created by Fortune on 7/12/2017.
 */
public class VerificationInterruptException extends RuntimeException {

    public VerificationInterruptException(String message){
        super(message);
    }

    public VerificationInterruptException(String message, Throwable cause){
        super(message,cause);
    }


}
