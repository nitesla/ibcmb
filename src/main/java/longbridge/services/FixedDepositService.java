package longbridge.services;

import longbridge.dtos.FixedDepositDTO;
import longbridge.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;

import java.util.List;
import java.util.Optional;

/**
 * Created by mac on 08/03/2018.
 */
public interface FixedDepositService {
     List<FixedDepositDTO> getFixedDepositDetials(String username) ;
     Page<FixedDepositDTO> getFixedDepositDetials(String cifId, Pageable pageable) ;
     Response liquidateDeposit(FixedDepositDTO fixedDepositDTO) ;
     Response addFund(FixedDepositDTO fixedDepositDTO) ;
     Response bookFixDeposit(FixedDepositDTO fixedDepositDTO) ;
     boolean isBalanceEnoughForBooking(FixedDepositDTO fixedDepositDTO) ;
     String sendMail(FixedDepositDTO fixedDepositDTO) ;
     Optional<Integer>getRateBasedOnAmountAndTenor(int amount, int tenor);
     FixedDepositDTO getFixedDepositDetails(String accountNumber);
     Page<FixedDepositDTO> getFixedDepositForView(String accountNumber,Pageable pageable) ;
     Page<FixedDepositDTO> getFixedDepositsForView(String cifId,Pageable pageable) ;
     void sendFixedDepositDetails(String recipient, String name, String accountNumber) throws MailException;
}
