package longbridge.exception;

/**
 * Created by ayoade_farooq@yhaoo.com on 5/9/2017.
 */
public class TransferAuthorizationException extends InternetBankingException {

    public TransferAuthorizationException(){super("");}

    public TransferAuthorizationException(String message){super(message);}

    public TransferAuthorizationException(String message, Throwable cause){super(message,cause);}



}
