package longbridge.exception;

/**
 * Created by Fortune on 5/30/2017.
 */
public class EntrustConnectionException  extends InternetBankingException{

    public EntrustConnectionException(String message){super(message);}

    public EntrustConnectionException(String messaage, Throwable cause){super(messaage,cause);};
}
