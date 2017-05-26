package longbridge.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
        String errorMessage = messages.getMessage("transfer.add.failure", null, locale);

        if (exception.getMessage().equalsIgnoreCase(TransferExceptions.BALANCE.toString())) {
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
        return errorMessage;

    }


}
