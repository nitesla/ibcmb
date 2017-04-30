package longbridge.services;

import longbridge.dtos.AccountLimitDTO;
import longbridge.dtos.ClassLimitDTO;
import longbridge.dtos.GlobalLimitDTO;
import longbridge.models.Account;
import longbridge.models.GlobalLimit;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

/**
 * Created by Fortune on 4/25/2017.
 */
public interface LimitService  {

    void addGlobalLimit(GlobalLimitDTO globalLimit)throws  Exception;

    void addClassLimit(ClassLimitDTO classLimit) throws  Exception;

    void addAccountLimit(AccountLimitDTO accountLimit)throws  Exception;


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
