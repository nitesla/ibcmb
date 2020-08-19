package longbridge.services;



import com.sun.mail.util.MailConnectException;
import longbridge.dtos.LoanDTO;
import longbridge.exception.InternetBankingException;
import org.springframework.mail.MailException;

import java.net.UnknownHostException;


public interface LoanDetailsService {

    void sendLoanDetails(String recipient, String name,String accountNumber) throws MailException;
    LoanDTO getLoanDetails(String accountNumber);




}
