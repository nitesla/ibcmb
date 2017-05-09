package longbridge.exception;

/**
 * Created by Fortune on 5/9/2017.
 */
public class InternetBankingPasswordPolicyViolationException extends InternetBankingPasswordException {

    public InternetBankingPasswordPolicyViolationException(){
        super("Password does not meet the password ploicy");
    }

    public InternetBankingPasswordPolicyViolationException(String message){
        super(message);
    }

}
