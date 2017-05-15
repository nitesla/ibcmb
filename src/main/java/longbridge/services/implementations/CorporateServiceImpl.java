package longbridge.services.implementations;

import longbridge.dtos.CorporateDTO;
import longbridge.exception.InternetBankingException;
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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    MessageSource messageSource;

    Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public CorporateServiceImpl(CorporateRepo corporateRepo, CorpLimitRepo corpLimitRepo){
        this.corporateRepo = corporateRepo;
        this.corpLimitRepo = corpLimitRepo;
    }
    @Override
    public String addCorporate(CorporateDTO corporateDTO) throws InternetBankingException {
        corporateDTO.setDateCreated(new Date());
        Corporate corporate = convertDTOToEntity(corporateDTO);
        corporateRepo.save(corporate);
        return messageSource.getMessage("corporate.add.success",null,locale);

    }

    @Override
    public String deleteCorporate(Long id) throws InternetBankingException {
        corporateRepo.delete(id);
        return messageSource.getMessage("corporate.delete.success",null,locale);
    }

    @Override
    public String updateCorporate(CorporateDTO corporateDTO) throws InternetBankingException{
        Corporate corporate = convertDTOToEntity(corporateDTO);
        corporateRepo.save(corporate);
        return messageSource.getMessage("corporate.update.success",null,locale);
    }

    @Override
    public CorporateDTO getCorporate(Long id) {
        Corporate corporate = corporateRepo.findOne(id);
        return convertEntityToDTO(corporate);
    }

    @Override
    public List<CorporateDTO> getCorporates() {
        Iterable<Corporate> corporateDTOS = corporateRepo.findAll();
        return convertEntitiesToDTOs(corporateDTOS);
    }


    @Override
    public boolean addAccount(Corporate corporate, Account account) throws  InternetBankingException {
        accountService.AddAccount(corporate.getCustomerId(),account);
        return true;
    }

    @Override
    public String addCorporateUser(Corporate corporate, CorporateUser corporateUser) throws InternetBankingException{
        corporate.getUsers().add(corporateUser);
        corporateRepo.save(corporate);
        return  messageSource.getMessage("user.create.success",null,locale);
    }


    @Override
    public void enableCorporate(Corporate corporate) throws InternetBankingException {
        corporate.setEnabled(true);
        corporateRepo.save(corporate);
    }

    @Override
    public void disableCorporate(Corporate corporate) throws InternetBankingException{
        corporate.setEnabled(false);
        corporateRepo.save(corporate);
    }

    @Override
    public void setLimit(Corporate corporate, CorpLimit limit) throws InternetBankingException{
        limit.setCorporate(corporate);
        corpLimitRepo.save(limit);
    }

    @Override
    public void updateLimit(Corporate corporate, CorpLimit limit) throws InternetBankingException {
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
