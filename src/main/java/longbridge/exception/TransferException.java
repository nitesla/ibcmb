package longbridge.exception;

import java.net.PortUnreachableException;

/**
 * Created by Fortune on 5/9/2017.
 */
public class TransferException extends InternetBankingException {

    public TransferException(){super("Failed to make transfer");}

    public TransferException(String message){super(message);}

    public TransferException(String message,Throwable cause){super(message,cause);}

}
