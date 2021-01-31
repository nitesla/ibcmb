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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

        try {
            NeftBank neftBank = convertDTOToEntity(code);
            System.out.println("This is the object : " + neftBank);
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
        } catch (Exception ex) {
            throw new InternetBankingException(messageSource.getMessage("neftBank.delete.failure", null, locale));
        }
    }

    @Override
    public NeftBankDTO getNeftBank(Long neftBankId) {
        return convertEntityToDTO(neftBankRepo.getOne(neftBankId));
    }

    @Override
    public List<NeftBankDTO> getNeftBranchesByBankName(String bankName) {
        return neftBankRepo.findAllByBankName(bankName).stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    @Override
    public String updateNeftBank(NeftBankDTO neftBankDTO) throws InternetBankingException {
        try {
            NeftBank neftBank = convertDTOToEntity(neftBankDTO);
            neftBankRepo.save(neftBank);
            return messageSource.getMessage("neftBank.update.success", null, locale);
        } catch (Exception ex) {
            throw new InternetBankingException(messageSource.getMessage("neftBank.update.failure", null, locale));
        }
    }

    @Override
    public Page<NeftBankDTO> getNeftBranchesByBankName(String bankName, Pageable pageDetails) {
        return neftBankRepo.findAllByBankName(bankName, pageDetails).map(this::convertEntityToDTO);
    }

    @Override
    public Page<NeftBankNameDTO> getNeftBankNames(Pageable pageDetails) {
        return neftBankRepo.findAllBanks(pageDetails).map(NeftBankNameDTO::new);
    }

    @Override
    public Page<NeftBankDTO> getNeftBanks(Pageable pageDetails) {
        return neftBankRepo.findAll(pageDetails).map(this::convertEntityToDTO);
    }

    @Override
    public List<NeftBankDTO> getNeftBankList() {
        return neftBankRepo.findAll().stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    @Override
    public Page<NeftBankNameDTO> getNeftBankNames(String pattern, Pageable pageable) {
        return neftBankRepo.searchByBank(pattern, pageable).map(NeftBankNameDTO::new);
    }

    private NeftBankDTO convertEntityToDTO(NeftBank neftBank) {
        return modelMapper.map(neftBank, NeftBankDTO.class);
    }

    private NeftBank convertDTOToEntity(NeftBankDTO neftBankDTO) {
        return modelMapper.map(neftBankDTO, NeftBank.class);
    }


}
