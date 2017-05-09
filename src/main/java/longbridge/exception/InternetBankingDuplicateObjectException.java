package longbridge.exception;

/**
 * Created by Fortune on 5/9/2017.
 */
public class InternetBankingDuplicateObjectException extends InternetBankingException {

    public InternetBankingDuplicateObjectException(){super("The target object already exists");}

    public InternetBankingDuplicateObjectException(String message){super(message);}
}
