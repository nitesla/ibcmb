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

    String updateGlobalLimit(GlobalLimitDTO globalLimitDTO) throws InternetBankingException;
    String addClassLimit(ClassLimitDTO classLimit) throws InternetBankingException;

    String updateClassLimit(ClassLimitDTO classLimitDTO) throws InternetBankingException;


    String addAccountLimit(AccountLimitDTO accountLimit) throws InternetBankingException;
    String updateAccountLimit(AccountLimitDTO accountLimitDTO) throws InternetBankingException;

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

    String deleteCorporateAccountLimit(Long id) throws InternetBankingException;
    String deleteCorporateClassLimit(Long id) throws InternetBankingException;
    String deleteCorporateGlobalLimit(Long id) throws InternetBankingException;
    String deleteRetailAccountLimit(Long id) throws InternetBankingException;
    String deleteRetailClassLimit(Long id) throws InternetBankingException;
    String deleteRetailGlobalLimit(Long id) throws InternetBankingException;


}
