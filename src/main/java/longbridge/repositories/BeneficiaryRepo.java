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
public interface BeneficiaryRepo<T extends Beneficiary,ID  extends Serializable> extends JpaRepository<T, ID>{

    Iterable<LocalBeneficiary> findByUser(RetailUser user);

    Iterable<LocalBeneficiary> findByUserAndDelFlag(RetailUser user, String delFlag);


}
