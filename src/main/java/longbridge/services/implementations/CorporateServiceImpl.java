package longbridge.services.implementations;

import longbridge.models.*;
import longbridge.services.CorporateService;

/**
 * Created by Fortune on 4/5/2017.
 */
public class CorporateServiceImpl implements CorporateService {

    @Override
    public void addCorporate(Corporate corporate) {

    }

    @Override
    public void deleteCorporate(Long corporate) {

    }

    @Override
    public void updateCorporate(Corporate corporate) {

    }

    @Override
    public Corporate getCorporate(Long id) {
        return null;
    }

    @Override
    public Iterable<Corporate> getCorporates() {
        return null;
    }

    @Override
    public void setLimit(Corporate corporate, CorpLimit limit) {

    }

    @Override
    public void updateLimit(Corporate corporate, CorpLimit limit) {

    }

    @Override
    public Iterable<CorpLimit> getLimit(Corporate corporate) {
        return null;
    }

    @Override
    public void deleteLimit(Long  corporateId, CorpLimit limit) {

    }

    @Override
    public void setCorporateUserLimit(CorporateUser corporateUser, double limitValue) {

    }

    @Override
    public void updateCorporateUserLimit(CorporateUser corporateUser, double limitValue) {

    }

    @Override
    public double getCorporateUserLimit(CorporateUser corporateUser) {
        return 0;
    }

    @Override
    public void deleteCorporateUserLimit(CorporateUser corporateUser) {

    }

    @Override
    public void addAccount(Corporate corporate, Account account) {

    }

    @Override
    public void addCorporateUser(Corporate corporate, CorporateUser corporateUser) {

    }

    @Override
    public void enableCorporate(Corporate corporate) {

    }

    @Override
    public void disableCorporate(Corporate corporate) {

    }
}
