package longbridge.exception;

/**
 * Created by Fortune on 5/20/2017.
 */
public class InvalidAuthorizationException extends InternetBankingException{

    public InvalidAuthorizationException(){super();}

    public InvalidAuthorizationException(String message){super(message);}
}
