package longbridge.exception;

/**
 * Created by Fortune on 5/9/2017.
 */
public class AccountFetchException extends InternetBankingException {

    public AccountFetchException(){super();}

    public AccountFetchException(String message){super(message);}

    public AccountFetchException(String message, Throwable cause){super(message,cause);}
}
