package longbridge.services.implementations;

import longbridge.models.AuditConfig;
import longbridge.repositories.AuditConfigRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ayoade_farooq@yahoo.com on 4/19/2017.
 */
@Service
public class AuditConfigImpl {

    @Autowired
    private AuditConfigRepo configRepo;


    public AuditConfig findEntity(String s){
        return configRepo.findByEntityName(s);
    }
}
