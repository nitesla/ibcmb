package longbridge.exception;

import java.math.BigDecimal;

/**
 * Created by Fortune on 5/20/2017.
 */
public class NoDefinedRoleException extends TransferRuleException{

    public NoDefinedRoleException(){super("No qualified authorizer found for the transfer request");}

    public NoDefinedRoleException(String message){super(message);}

    public NoDefinedRoleException(BigDecimal amount){super("No qualified authorizer found for the transfer of amount "+amount.toString());}

}
