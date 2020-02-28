package longbridge.services;

import longbridge.dtos.FixedDepositDTO;
import longbridge.exception.InternetBankingException;
import longbridge.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Created by mac on 08/03/2018.
 */
public interface FixedDepositService {
     List<FixedDepositDTO> getFixedDepositDetials(String username) throws InternetBankingException;
     Page<FixedDepositDTO> getFixedDepositDetials(String cifId, Pageable pageable) throws InternetBankingException;
     Response liquidateDeposit(FixedDepositDTO fixedDepositDTO) throws InternetBankingException;
     Response addFund(FixedDepositDTO fixedDepositDTO) throws InternetBankingException;
     Response bookFixDeposit(FixedDepositDTO fixedDepositDTO) throws InternetBankingException;
     boolean isBalanceEnoughForBooking(FixedDepositDTO fixedDepositDTO) throws InternetBankingException;
     String sendMail(FixedDepositDTO fixedDepositDTO) throws InternetBankingException;
     Optional<Integer>getRateBasedOnAmountAndTenor(int amount, int tenor);
}
