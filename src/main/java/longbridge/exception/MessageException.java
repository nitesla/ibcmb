package longbridge.exception;

/**
 * Created by Fortune on 5/12/2017.
 */
public class MessageException extends InternetBankingException {

   public MessageException(){super("Could not send message");}

    public  MessageException(String message){super(message);}

   public  MessageException(String message,Throwable cause){super(message,cause);}

}
