package longbridge.services;

import longbridge.dtos.AccountLimitDTO;
import longbridge.dtos.ClassLimitDTO;
import longbridge.dtos.GlobalLimitDTO;
import longbridge.exception.InternetBankingException;

import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */
public interface TransactionLimitService {

    String addGlobalLimit(GlobalLimitDTO globalLimit) throws InternetBankingException;

    String addClassLimit(ClassLimitDTO classLimit) throws InternetBankingException;

    String addAccountLimit(AccountLimitDTO accountLimit) throws InternetBankingException;


    GlobalLimitDTO getCorporateGlobalLimit(Long id);

    ClassLimitDTO getCorporateClassLimit(Long id);

    AccountLimitDTO getCorporateAccountLimit(Long id);

    GlobalLimitDTO getRetailGlobalLimit(Long id);

    ClassLimitDTO getRetailClassLimit(Long id);

    AccountLimitDTO getRetailAccountLimit(Long id);


    List<GlobalLimitDTO> getCorporateGlobalLimits();

    List<ClassLimitDTO> getCorporateClassLimits();

    List<AccountLimitDTO> getCorporateAccountLimits();


    List<GlobalLimitDTO> getRetailGlobalLimits();

    List<ClassLimitDTO> getRetailClassLimits();

    List<AccountLimitDTO> getRetailAccountLimits();


}
