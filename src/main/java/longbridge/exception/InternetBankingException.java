package longbridge.exception;

/**
 * Created by Fortune on 5/8/2017.
 */
public class InternetBankingException extends RuntimeException {

    public InternetBankingException(){super("Failed to perform the requested action");}

    public InternetBankingException(String message){super(message);}
}
