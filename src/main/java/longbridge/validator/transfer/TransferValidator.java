package longbridge.validator.transfer;

/**
 * Created by ayoade_farooq@yahoo.com on 5/8/2017.
 */

import longbridge.dtos.TransferRequestDTO;
import longbridge.models.Account;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.StreamSupport;

@Component
public class TransferValidator implements Validator {

    private AccountService accountService;
    private IntegrationService integrationService;

    @Autowired
    public TransferValidator(AccountService accountService, IntegrationService integrationService) {
        this.accountService = accountService;
        this.integrationService = integrationService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return TransferRequestDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "financialInstitution", "message.financialInstitution", "financialInstitution is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "beneficiaryAccountNumber", "message.beneficiaryAccountNumber", "beneficiaryAccountNumber is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "amount", "message.amount", "amount is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "customerAccountNumber", "message.customerAccountNumber", "customerAccountNumber is required.");
        TransferRequestDTO request = (TransferRequestDTO) o;
         if (sameAccount(request)){
             errors.reject("customerAccountNumber", "message.sameaccount");
         }


         if (!isDebitAllowed(request.getCustomerAccountNumber())){
             errors.reject("customerAccountNumber", "message.nodebit");
         }
         if( !hasSufficientBalance(request)){
             errors.reject("amount", "message.insufficient");
         }

    }


    boolean sameAccount(TransferRequestDTO dto) {
        if (dto.getBeneficiaryAccountNumber().equalsIgnoreCase(dto.getCustomerAccountNumber())) {
            return true;
        }
        return false;
    }


    boolean isDebitAllowed(String s) {
        String cif = accountService.getAccountByAccountNumber(s).getCustomerId();
        Account acct = StreamSupport.stream(accountService.getAccountsForDebit(cif).spliterator(), false)
                .filter(i -> i.getAccountNumber().equalsIgnoreCase(s))
                .findFirst()
                .orElse(null);

        if (acct != null) {
            return true;
        }
        return false;
    }

    boolean hasSufficientBalance(TransferRequestDTO dto) {
   BigDecimal balance = integrationService.getAvailableBalance(dto.getCustomerAccountNumber());
    BigDecimal amount = dto.getAmount();
    boolean ok = balance.compareTo(amount)==1;
    return ok;
    }


}
