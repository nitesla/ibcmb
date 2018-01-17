package longbridge.services;

import longbridge.dtos.AccountLimitDTO;
import longbridge.dtos.ClassLimitDTO;
import longbridge.dtos.GlobalLimitDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.UserType;
import longbridge.utils.TransferType;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */
public interface TransactionLimitService {

    @PreAuthorize("hasAuthority('ADD_TRANS_LIMIT')")
    String addGlobalLimit(GlobalLimitDTO globalLimit) throws InternetBankingException;

    @PreAuthorize("hasAuthority('UPDATE_TRANS_LIMIT')")
    String updateGlobalLimit(GlobalLimitDTO globalLimitDTO) throws InternetBankingException;

    @PreAuthorize("hasAuthority('ADD_TRANS_LIMIT')")
    String addClassLimit(ClassLimitDTO classLimit) throws InternetBankingException;

    @PreAuthorize("hasAuthority('UPDATE_TRANS_LIMIT')")
    String updateClassLimit(ClassLimitDTO classLimitDTO) throws InternetBankingException;

    @PreAuthorize("hasAuthority('ADD_TRANS_LIMIT')")
    String addAccountLimit(AccountLimitDTO accountLimit) throws InternetBankingException;

    @PreAuthorize("hasAuthority('UPDATE_TRANS_LIMIT')")
    String updateAccountLimit(AccountLimitDTO accountLimitDTO) throws InternetBankingException;

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    GlobalLimitDTO getCorporateGlobalLimit(Long id);

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    ClassLimitDTO getCorporateClassLimit(Long id);

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    AccountLimitDTO getCorporateAccountLimit(Long id);

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    GlobalLimitDTO getRetailGlobalLimit(Long id);

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    ClassLimitDTO getRetailClassLimit(Long id);

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    AccountLimitDTO getRetailAccountLimit(Long id);

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    List<GlobalLimitDTO> getCorporateGlobalLimits();

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    List<ClassLimitDTO> getCorporateClassLimits();

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    List<AccountLimitDTO> getCorporateAccountLimits();

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    List<GlobalLimitDTO> getRetailGlobalLimits();

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    List<ClassLimitDTO> getRetailClassLimits();

    @PreAuthorize("hasAuthority('GET_TRANS_LIMIT')")
    List<AccountLimitDTO> getRetailAccountLimits();

    @PreAuthorize("hasAuthority('DELETE_TRANS_LIMIT')")
    String deleteCorporateAccountLimit(Long id) throws InternetBankingException;

    @PreAuthorize("hasAuthority('DELETE_TRANS_LIMIT')")
    String deleteCorporateClassLimit(Long id) throws InternetBankingException;

    @PreAuthorize("hasAuthority('DELETE_TRANS_LIMIT')")
    String deleteCorporateGlobalLimit(Long id) throws InternetBankingException;

    @PreAuthorize("hasAuthority('DELETE_TRANS_LIMIT')")
    String deleteRetailAccountLimit(Long id) throws InternetBankingException;

    @PreAuthorize("hasAuthority('DELETE_TRANS_LIMIT')")
    String deleteRetailClassLimit(Long id) throws InternetBankingException;

    @PreAuthorize("hasAuthority('DELETE_TRANS_LIMIT')")
    String deleteRetailGlobalLimit(Long id) throws InternetBankingException;

    boolean isAboveInternetBankingLimit(TransferType transferType, UserType customerType, String accountNumber, BigDecimal amount);

}