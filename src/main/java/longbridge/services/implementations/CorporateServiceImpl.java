package longbridge.services.implementations;

import longbridge.models.*;
import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.services.AccountService;
import longbridge.services.CorporateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Fortune on 4/5/2017.
 */
@Service
public class CorporateServiceImpl implements CorporateService {

    private CorporateRepo corporateRepo;
    private CorpLimitRepo corpLimitRepo;
    @Autowired
    private CorporateUserRepo corporateUserRepo;
    @Autowired
    private AccountService accountService;


    @Autowired
    public CorporateServiceImpl(CorporateRepo corporateRepo, CorpLimitRepo corpLimitRepo){
        this.corporateRepo = corporateRepo;
        this.corpLimitRepo = corpLimitRepo;
    }
    @Override
    public void addCorporate(Corporate corporate) {
        corporateRepo.save(corporate);
    }

    @Override
    public void deleteCorporate(Long corporate) {
        corporateRepo.delete(corporateRepo.findOne(corporate));
    }

    @Override
    public void updateCorporate(Corporate corporate) {
        corporateRepo.save(corporate);
    }

    @Override
    public Corporate getCorporate(Long id) {
        return corporateRepo.findOne(id);
    }

    @Override
    public List<Corporate> getCorporates() {
        return corporateRepo.findAll();
    }


    @Override
    public void addAccount(Corporate corporate, Account account) {
        accountService.AddAccount(corporate.getCustomerId(),account);
    }

    @Override
    public void addCorporateUser(Corporate corporate, CorporateUser corporateUser) {
        corporate.getUsers().add(corporateUser);
        corporateRepo.save(corporate);
    }


    @Override
    public void enableCorporate(Corporate corporate) {
        corporate.setEnabled(true);
        corporateRepo.save(corporate);
    }

    @Override
    public void disableCorporate(Corporate corporate) {
        corporate.setEnabled(false);
        corporateRepo.save(corporate);
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
}
