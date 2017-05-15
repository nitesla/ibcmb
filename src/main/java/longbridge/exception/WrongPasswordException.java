package longbridge.exception;

/**
 * Created by Fortune on 5/10/2017.
 */
public class WrongPasswordException extends PasswordException{

    public WrongPasswordException(){super("Wrong old password provided");}


    public WrongPasswordException(String message){super(message);}


}
