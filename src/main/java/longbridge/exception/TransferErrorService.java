package longbridge.exception;

import longbridge.models.TransferCodeTransalator;
import longbridge.repositories.TransferCodeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Created by ayoade_farooq@yahoo.com on 4/20/2017.
 */
@Service
public class TransferErrorService {


    private MessageSource messages;
    private TransferCodeRepo transferCodeRepo;
    final Locale locale = LocaleContextHolder.getLocale();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    @Autowired
    public void setTransferCodeRepo(TransferCodeRepo transferCodeRepo) {
        this.transferCodeRepo = transferCodeRepo;
    }

    public String getMessage(InternetBankingTransferException exception) {
        String errorMessage;

        try {
            TransferCodeTransalator codeTransalator = transferCodeRepo.findFirstByResponseCode(exception.getMessage());
            if (codeTransalator != null) {
                String error = codeTransalator.getResponseMessage();
                return messages.getMessage(error, null, locale);
            }

            errorMessage = messages.getMessage(exception.getMessage(), null, locale);

        } catch (Exception e) {
            logger.error("Error resolving transfer error code",e);
            errorMessage = messages.getMessage("transfer.error", null, locale);

        }

        return errorMessage;

    }
    public String getMessage(String errorCode){
        logger.info("ERROR CODE {} " , errorCode);
       String error="";
        try {
            TransferCodeTransalator codeTransalator = transferCodeRepo.findFirstByResponseCode(errorCode);
            if (codeTransalator != null) {
                 error = codeTransalator.getResponseDesc();
                return error;
            }


        } catch (Exception e) {
            logger.error("Error resolving transfer error code",e);

        }

        return error;




    }



//    public String getExactMessage(String exception){
//        final Locale locale = LocaleContextHolder.getLocale();
//
//        String errorMessage ;
//
//        try{
//            errorMessage = messages.getMessage(exception, null, locale);
//        }catch (Exception e)
//        {
//            errorMessage=exception;
//        }
//
//        return errorMessage;
//
//    }

}
