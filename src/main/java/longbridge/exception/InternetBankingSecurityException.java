package longbridge.exception;

/**
 * Created by ayoade_farooq@yhaoo.com on 5/9/2017.
 */
public class InternetBankingSecurityException extends InternetBankingException {

    public InternetBankingSecurityException(){super("");}

    public InternetBankingSecurityException(String message){super(message);}

    public InternetBankingSecurityException(String message, Throwable cause){super(message,cause);}

}
