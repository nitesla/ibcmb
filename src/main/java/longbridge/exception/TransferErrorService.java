package longbridge.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by ayoade_farooq@yahoo.com on 4/20/2017.
 */
@Service
public class TransferErrorService {

    @Autowired
    private MessageSource messages;

    @Autowired
    private LocaleResolver localeResolver;
    public String getMessage(InternetBankingTransferException exception, HttpServletRequest request){
        final Locale locale = localeResolver.resolveLocale(request);
       // String errorMessage = messages.getMessage("transfer.api.failure", null, locale) ;







        String errorMessage ;

       /* if (exception.getMessage().equalsIgnoreCase(TransferExceptions.BALANCE.toString()))
        {
            errorMessage = messages.getMessage(TransferExceptions.BALANCE.toString(), null, locale);
        } else if (exception.getMessage().equalsIgnoreCase(TransferExceptions.NO_DEBIT_ACCOUNT.toString())) {
            errorMessage = messages.getMessage(TransferExceptions.NO_DEBIT_ACCOUNT.toString(), null, locale);
        } else if (exception.getMessage().equalsIgnoreCase(TransferExceptions.SAME_ACCOUNT.toString())) {
            errorMessage = messages.getMessage(TransferExceptions.SAME_ACCOUNT.toString(), null, locale);
        }else if (exception.getMessage().equalsIgnoreCase(TransferExceptions.INVALID_ACCOUNT.toString())) {
            errorMessage = messages.getMessage(TransferExceptions.INVALID_ACCOUNT.toString(), null, locale);
        }else if (exception.getMessage().equalsIgnoreCase(TransferExceptions.INVALID_BENEFICIARY.toString())) {
            errorMessage = messages.getMessage(TransferExceptions.INVALID_BENEFICIARY.toString(), null, locale);
        }
        else if (exception.getMessage().equalsIgnoreCase(TransferExceptions.LIMIT_EXCEEDED.toString())) {
            errorMessage = messages.getMessage(TransferExceptions.LIMIT_EXCEEDED.toString(), null, locale);
        }
        else if (exception.getMessage().equalsIgnoreCase(TransferExceptions.INVALID_AMOUNT.toString())) {
            errorMessage = messages.getMessage(TransferExceptions.INVALID_AMOUNT.toString(), null, locale);
        }*/



       try{
           errorMessage = messages.getMessage(exception.getMessage(), null, locale);
       }catch (Exception e){
           errorMessage=exception.getMessage();
       }

        return errorMessage;

    }
    public String getExactMessage(String exception){
        final Locale locale = LocaleContextHolder.getLocale();

        String errorMessage ;

        try{
            errorMessage = messages.getMessage(exception, null, locale);
        }catch (Exception e)
        {
            errorMessage=exception;
        }

        return errorMessage;

    }

}
