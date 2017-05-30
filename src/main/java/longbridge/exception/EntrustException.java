package longbridge.exception;

/**
 * Created by Fortune on 5/30/2017.
 */
public class EntrustException extends InternetBankingException{

    public EntrustException(String message){super(message);}

    public EntrustException(String message, Throwable cause){super(message,cause);}
}
