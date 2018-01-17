package longbridge.exception;

/**
 * Created by Fortune on 5/9/2017.
 */
public class PasswordPolicyViolationException extends PasswordException {

    public PasswordPolicyViolationException(){
        super("Password does not meet the password ploicy");
    }

    public PasswordPolicyViolationException(String message){super(message);}



}
