package longbridge.services.implementations;

import longbridge.dtos.CorporateDTO;
import longbridge.models.Account;
import longbridge.models.CorpLimit;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.repositories.CorpLimitRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CorporateUserRepo;
import longbridge.services.AccountService;
import longbridge.services.CorporateService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    ModelMapper modelMapper;

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

	@Override
	public Page<CorporateDTO> getCorporates(Pageable pageDetails) {
        Page<Corporate> page = corporateRepo.findAll(pageDetails);
        List<CorporateDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<CorporateDTO> pageImpl = new PageImpl<CorporateDTO>(dtOs,pageDetails,t);
        return pageImpl;
	}

    private CorporateDTO convertEntityToDTO(Corporate corporate){
        return  modelMapper.map(corporate,CorporateDTO.class);
    }

    private Corporate convertDTOToEntity(CorporateDTO corporateDTO){
        return  modelMapper.map(corporateDTO,Corporate.class);
    }

    private List<CorporateDTO> convertEntitiesToDTOs(Iterable<Corporate> corporates){
        List<CorporateDTO> corporateDTOList = new ArrayList<>();
        for(Corporate corporate: corporates){
            CorporateDTO corporateDTO = convertEntityToDTO(corporate);
            corporateDTOList.add(corporateDTO);
        }
        return corporateDTOList;
    }
}
