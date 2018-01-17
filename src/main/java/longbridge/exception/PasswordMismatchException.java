package longbridge.exception;

/**
 * Created by Fortune on 5/10/2017.
 */
public class PasswordMismatchException extends PasswordException{

    public PasswordMismatchException(){super("Passwords do not match");}


    public PasswordMismatchException(String message){super(message);}


}
