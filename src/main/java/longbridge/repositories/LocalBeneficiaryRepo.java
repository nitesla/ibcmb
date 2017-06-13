package longbridge.repositories;

import longbridge.models.LocalBeneficiary;
import longbridge.models.RetailUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Fortune on 4/4/2017.
 */
public interface LocalBeneficiaryRepo extends CommonRepo<LocalBeneficiary,Long>{

    Iterable<LocalBeneficiary> findByUser(RetailUser user);
    LocalBeneficiary findByUser_IdAndAccountNumber(Long id,String acctNo);
    List<LocalBeneficiary> findByUser_IdAndBeneficiaryBankIgnoreCase(Long id,String bankCode);
    List<LocalBeneficiary> findByUser_IdAndBeneficiaryBankIgnoreCaseNot(Long id,String bankCode);

    List<LocalBeneficiary> findByUserAndBeneficiaryBankIgnoreCaseNot(RetailUser user,String bankCode);
    List<LocalBeneficiary> findByUserAndBeneficiaryBankIgnoreCase(RetailUser user,String bankCode);
    List<LocalBeneficiary> findByUserAndBeneficiaryBankIsNotNull(RetailUser user);


    Iterable<LocalBeneficiary> findByUserAndDelFlag(RetailUser user, String delFlag);

}
