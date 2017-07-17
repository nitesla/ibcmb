package longbridge.exception;

/**
 * Created by Fortune on 7/12/2017.
 */
public class VerificationInterruptedException extends RuntimeException {

    public VerificationInterruptedException(String message){
        super(message);
    }

    public VerificationInterruptedException(String message, Throwable cause){
        super(message,cause);
    }


}
