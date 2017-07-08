package longbridge.exception;

import java.math.BigDecimal;

/**
 * Created by Fortune on 5/20/2017.
 */
public class NoDefinedRoleException extends TransferRuleException{

    public NoDefinedRoleException(){}

    public NoDefinedRoleException(String message){super(message);}

    public NoDefinedRoleException(String message,BigDecimal amount){
        super(message+" "+amount.toString());
    }

}
