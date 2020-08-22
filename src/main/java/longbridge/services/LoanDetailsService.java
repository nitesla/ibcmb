package longbridge.services;




import longbridge.dtos.LoanDTO;
import org.springframework.mail.MailException;




public interface LoanDetailsService {

    void sendLoanDetails(String recipient, String name,String accountNumber) throws MailException;
    LoanDTO getLoanDetails(String accountNumber);




}
