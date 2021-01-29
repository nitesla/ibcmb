package longbridge.services.implementations;

import longbridge.dtos.NeftBankDTO;
import longbridge.dtos.NeftBankNameDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.NeftBank;
import longbridge.repositories.NeftBankRepo;
import longbridge.services.NeftBankService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class NeftBankServiceImpl implements NeftBankService {

    @Autowired
    private NeftBankRepo neftBankRepo;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MessageSource messageSource;
    private Locale locale;
    private Logger logger = LoggerFactory.getLogger(NeftBankServiceImpl.class);


    @Override
    public String addNeftBank(NeftBankDTO code) throws InternetBankingException {

        try{
            NeftBank neftBank = convertDTOToEntity(code);
            System.out.println("This is the object : "+ neftBank);
            neftBankRepo.save(neftBank);
            logger.info("Added new neftBank {} with branch name {}", neftBank.getBankName(), neftBank.getBranchName());
            return messageSource.getMessage("neftBank.add.success", null, locale);

        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("neftBank.add.failure", null, locale), e);
        }
    }

    @Override
    public String deletNeftBank(Long neftBankId) throws InternetBankingException {
        try {
            neftBankRepo.deleteById(neftBankId);
            return messageSource.getMessage("neftBank.delete.success", null, locale);
        }catch (Exception ex){
            throw  new InternetBankingException(messageSource.getMessage("neftBank.delete.failure", null, locale));
        }
    }

    @Override
    public NeftBankDTO getNeftBank(Long neftBankId) {
        return convertEntityToDTO(neftBankRepo.getOne(neftBankId));
    }

    @Override
    public List<NeftBankDTO> getNeftBranchesByBankName(String bankName) {
        return convertEntitiesToDTOs(neftBankRepo.findAllByBankName(bankName));
    }

    @Override
    public String updateNeftBank(NeftBankDTO neftBankDTO) throws InternetBankingException {
        try{
            NeftBank neftBank = convertDTOToEntity(neftBankDTO);
            neftBankRepo.save(neftBank);
            return messageSource.getMessage("neftBank.update.success", null, locale);
        }catch (Exception ex){
            throw  new InternetBankingException(messageSource.getMessage("neftBank.update.failure", null, locale));
        }
    }

    @Override
    public Page<NeftBankDTO> getNeftBranchesByBankName(String bankName, Pageable pageDetails) {
        Page<NeftBank> page = neftBankRepo.findAllByBankName(bankName, pageDetails);
        List<NeftBankDTO> dtOs = getNeftBranchesByBankName(bankName);
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);
    }

    @Override
    public Page<NeftBankNameDTO> getNeftBankNames(Pageable pageDetails) {
        List<NeftBank> neftBanks = neftBankRepo.findAll();
        List<NeftBankNameDTO> bankNames = neftBanks.stream()
                .map(NeftBank::getBankName)
                .collect(Collectors.toSet())
                .stream()
                .map(NeftBankNameDTO::new)
                .collect(Collectors.toList());
        return new PageImpl<>(bankNames, pageDetails, bankNames.size());
    }

    @Override
    public Page<NeftBankDTO> getNeftBanks(Pageable pageDetails) {
        Page<NeftBank> page = neftBankRepo.findAll(pageDetails);
        List<NeftBankDTO> dtOs = convertEntitiesToDTOs(page);
        long t = page.getTotalElements();
        return new PageImpl<>(dtOs, pageDetails, t);
    }

    @Override
    public Iterable<NeftBankDTO> getNeftBanks() {
        return convertEntitiesToDTOs(neftBankRepo.findAll());
    }

    @Override
    public List<NeftBankDTO> getNeftBankList() {
        return convertEntitiesToDTOs(neftBankRepo.findAll());
    }

    @Override
    public NeftBankDTO convertEntityToDTO(NeftBank neftBank) {
        return modelMapper.map(neftBank, NeftBankDTO.class);
    }

    @Override
    public NeftBank convertDTOToEntity(NeftBankDTO neftBankDTO) {
        return modelMapper.map(neftBankDTO, NeftBank.class);
    }

    @Override
    public List<NeftBankDTO> convertEntitiesToDTOs(Iterable<NeftBank> neftBanks) {
        return StreamSupport.stream(neftBanks.spliterator(), false)
                .map(this::convertEntityToDTO)
                .collect(Collectors.toList());
    }

}
