package longbridge.exception;

/**
 * Created by Fortune on 5/9/2017.
 */
public class IdentificationException extends InternetBankingException {

    public IdentificationException(){super();}

    public IdentificationException(String message){super(message);}

    public IdentificationException(String message, Throwable cause){super(message,cause);}
}
