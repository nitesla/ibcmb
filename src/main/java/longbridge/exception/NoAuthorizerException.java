package longbridge.exception;

import java.math.BigDecimal;

/**
 * Created by Fortune on 5/20/2017.
 */
public class NoAuthorizerException extends TransferRuleException{

    public  NoAuthorizerException(){super("No qualified authorizer found for the transfer request");}

    public NoAuthorizerException(String message){super(message);}

    public NoAuthorizerException(BigDecimal amount){super("No qualified authorizer found for the transfer of amount "+amount.toString());}

}
