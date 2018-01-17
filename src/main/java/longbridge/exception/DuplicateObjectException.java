package longbridge.exception;

/**
 * Created by Fortune on 5/9/2017.
 */
public class DuplicateObjectException extends InternetBankingException {

    public DuplicateObjectException(){super("The target object already exists");}

    public DuplicateObjectException(String message){super(message);}
}
