package longbridge.repositories;

import longbridge.models.*;
import org.apache.tomcat.jni.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface InternationalBeneficiaryRepo extends CommonRepo<InternationalBeneficiary, Long>{

    Iterable<InternationalBeneficiary> findByUser(RetailUser user);
    InternationalBeneficiary findByUser_IdAndAccountNumber(Long id,String acctNo);

    Iterable<InternationalBeneficiary> findByUserAndDelFlag(RetailUser user, String delFlag);


}
