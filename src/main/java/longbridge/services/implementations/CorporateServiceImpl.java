package longbridge.services.implementations;

import longbridge.dtos.CorpLimitDTO;
import longbridge.models.*;
import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.CorporateCustomerRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.services.CorporateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Fortune on 4/5/2017.
 */
@Service
public class CorporateServiceImpl implements CorporateService {

    private CorporateCustomerRepo corporateCustomerRepo;

    private CorpLimitRepo corpLimitRepo;


    @Autowired
    public CorporateServiceImpl(CorporateCustomerRepo corporateCustomerRepo, CorpLimitRepo corpLimitRepo){
        this.corporateCustomerRepo = corporateCustomerRepo;
        this.corpLimitRepo = corpLimitRepo;
    }
    @Override
    public void addCorporate(Corporate corporate) {
        corporateCustomerRepo.save(corporate);
    }

    @Override
    public void deleteCorporate(Long corporate) {
        corporateCustomerRepo.delete(corporateCustomerRepo.findOne(corporate));
    }

    @Override
    public void updateCorporate(Corporate corporate) {
        corporateCustomerRepo.save(corporate);
    }

    @Override
    public Corporate getCorporate(Long id) {
        return corporateCustomerRepo.findOne(id);
    }

    @Override
    public List<Corporate> getCorporates() {
        return corporateCustomerRepo.findAll();
    }

    @Override
    public void setLimit(Corporate corporate, CorpLimit limit) {
        limit.setCorporate(corporate);
        corpLimitRepo.save(limit);
    }

    @Override
    public void updateLimit(Corporate corporate, CorpLimit limit) {
        limit.setCorporate(corporate);
        corpLimitRepo.save(limit);
    }

    @Override
    public List<CorpLimit> getLimit(Corporate corporate) {
        return corpLimitRepo.findByCorporate(corporate);
    }

    @Override
    public void deleteLimit(Long corporateId, CorpLimit limit) {
        limit.setDelFlag("Y");
        corpLimitRepo.save(limit);
    }

    @Override
    public void setCorporateUserLimit(CorporateUser corporateUser, double limitValue) {
        Corporate corporate = corporateUser.getCorporate();
        CorpLimit corpLimit = new CorpLimit();
        corpLimit.setUpperLimit(limitValue);
        corpLimit.setUpperLimit(0.0);
        corpLimit.setCorporate(corporate);
        corpLimitRepo.save(corpLimit);
    }

    @Override
    public void updateCorporateUserLimit(CorporateUser corporateUser, double limitValue) {
        Corporate corporate = corporateUser.getCorporate();
        CorpLimit corpLimit = new CorpLimit();
        corpLimit.setUpperLimit((limitValue));
        corpLimit.setUpperLimit(0.0);
        corpLimit.setCorporate(corporate);
        corpLimitRepo.save(corpLimit);
    }

    @Override
    public double getCorporateUserLimit(CorporateUser corporateUser) {
        Corporate corporate = corporateUser.getCorporate();
        List<CorpLimit> corpLimit = corpLimitRepo.findByCorporate(corporate);

        if(corpLimit.isEmpty()){
            return 0.0;
        }
        return (double) corpLimit.get(0).getUpperLimit();
    }


    @Override
    public void deleteCorporateUserLimit(CorporateUser corporateUser) {
        Corporate corporate = corporateUser.getCorporate();
        List<CorpLimit> corpLimit = corpLimitRepo.findByCorporate(corporate);

        if( corpLimit.isEmpty()){
            return;
        }
        corpLimit.get(0).setDelFlag("Y");
    }

    @Override
    public void addAccount(Corporate corporate, Account account) {
        //todo
    }

    @Override
    public void addCorporateUser(Corporate corporate, CorporateUser corporateUser) {
        corporate.getUsers().add(corporateUser);
        corporateCustomerRepo.save(corporate);
    }

    @Override
    public void enableCorporate(Corporate corporate) {
        corporate.setEnabled(true);
        corporateCustomerRepo.save(corporate);
    }

    @Override
    public void disableCorporate(Corporate corporate) {
        corporate.setEnabled(false);
        corporateCustomerRepo.save(corporate);
    }
}
