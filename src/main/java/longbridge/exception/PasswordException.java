package longbridge.exception;

/**
 * Created by Fortune on 5/9/2017.
 */
public class PasswordException extends InternetBankingException{

    public PasswordException(){super("Failed to process password");}

    public PasswordException(Throwable cause){super("Failed to process password",cause);}


    public PasswordException(String message){super(message);}

    public PasswordException(String message, Throwable cause){super(message,cause);}

}
