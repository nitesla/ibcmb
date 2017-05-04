package longbridge.repositories;

import longbridge.models.PersonnelContact;
import org.springframework.stereotype.Repository;

/**
 * Created by Fortune on 4/26/2017.
 */
@Repository
public interface UnitPersonnelRepo extends CommonRepo<PersonnelContact, Long>{

    Iterable<PersonnelContact> findByUnit(String unitName);
}
