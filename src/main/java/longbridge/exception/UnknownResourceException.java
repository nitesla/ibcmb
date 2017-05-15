package longbridge.exception;

/**
 * Created by Fortune on 5/8/2017.
 */
public class UnknownResourceException extends RuntimeException {

    public UnknownResourceException(){super("PAGE NOT FOUND");}

    public UnknownResourceException(String message){super(message);}
}
