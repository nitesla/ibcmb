package longbridge.services.implementations;

import longbridge.dtos.QuicktellerBankCodeDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.QuicktellerBankCode;
import longbridge.repositories.QuicktellerBankCodeRepo;
import longbridge.services.IntegrationService;
import longbridge.services.QuicktellerBankCodeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by Wunmi Sowunmi on 24/04/2017.
 */
@Service
public class QuicktellerBankCodeServiceImpl implements QuicktellerBankCodeService {

    private final QuicktellerBankCodeRepo quicktellerBankCodeRepo;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    public QuicktellerBankCodeServiceImpl(QuicktellerBankCodeRepo quicktellerBankCodeRepo) {
        this.quicktellerBankCodeRepo = quicktellerBankCodeRepo;
    }

    @Override
    public List<QuicktellerBankCodeDTO> convertEntitiesToDTOs(Iterable<QuicktellerBankCode> quicktellerBankCodes) {
        List<QuicktellerBankCodeDTO> quicktellerBankCodeDTOList = new ArrayList<>();
        for (QuicktellerBankCode quicktellerBankCode : quicktellerBankCodes) {
            QuicktellerBankCodeDTO fiDTO = convertEntityToDTO(quicktellerBankCode);
            quicktellerBankCodeDTOList.add(fiDTO);
        }
        return quicktellerBankCodeDTOList;
    }

    @Override
    public QuicktellerBankCodeDTO convertEntityToDTO(QuicktellerBankCode quicktellerBankCode) {
        QuicktellerBankCodeDTO quicktellerBankCodeDTO = new QuicktellerBankCodeDTO();
//        quicktellerBankCodeDTO.setId(quicktellerBankCode.getId());
//        quicktellerBankCodeDTO.setVersion(quicktellerBankCode.getVersion());
        quicktellerBankCodeDTO.setBankCodeId(quicktellerBankCode.getBankCodeId());
        quicktellerBankCodeDTO.setCbnCode(quicktellerBankCode.getCbnCode());
        quicktellerBankCodeDTO.setBankName(quicktellerBankCode.getBankName());
        quicktellerBankCodeDTO.setBankCode(quicktellerBankCode.getBankCode());
        return quicktellerBankCodeDTO;
    }

    @Override
    public QuicktellerBankCode convertDTOToEntity(QuicktellerBankCodeDTO quicktellerBankCodeDTO) {
        QuicktellerBankCode quicktellerBankCode = new QuicktellerBankCode();
        quicktellerBankCode.setBankCode(quicktellerBankCodeDTO.getBankCode());
        quicktellerBankCode.setCbnCode(quicktellerBankCodeDTO.getCbnCode());
        quicktellerBankCode.setBankName(quicktellerBankCodeDTO.getBankName());
        quicktellerBankCode.setBankCodeId(quicktellerBankCodeDTO.getBankCodeId());
        return modelMapper.map(quicktellerBankCodeDTO, QuicktellerBankCode.class);
    }

    @Override
    public String addQuicktellerBankCode(QuicktellerBankCodeDTO quicktellerBankCodeDTO) throws InternetBankingException {

        QuicktellerBankCode quicktellerBankCode;
        quicktellerBankCode = quicktellerBankCodeRepo.findByBankCodeAndCbnCode(quicktellerBankCodeDTO.getBankCode(), quicktellerBankCodeDTO.getCbnCode());
        if (quicktellerBankCode != null) {
            throw new DuplicateObjectException(messageSource.getMessage("quickbankcode.exists", null, locale));
        }
        try {
            quicktellerBankCode = new QuicktellerBankCode();
            quicktellerBankCode.setBankCodeId(quicktellerBankCodeDTO.getBankCodeId());
            quicktellerBankCode.setCbnCode(quicktellerBankCodeDTO.getCbnCode());
            quicktellerBankCode.setBankName(quicktellerBankCodeDTO.getBankName());
            quicktellerBankCode.setBankCode(quicktellerBankCodeDTO.getBankCode());
            this.quicktellerBankCodeRepo.save(quicktellerBankCode);
            logger.info("New quickteller bank name: {} created", quicktellerBankCode.getBankName());
            return messageSource.getMessage("quickbankcode.add.success", null, locale);
        }

        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("quickbankcode.add.failure", null, locale),e);
        }
    }

    @Override
    public String updateQuicktellerBankCode(QuicktellerBankCodeDTO quicktellerBankCodeDTO) throws InternetBankingException {
        try {
            QuicktellerBankCode quicktellerBankCode = new QuicktellerBankCode();
            quicktellerBankCode.setId((quicktellerBankCodeDTO.getId()));
            quicktellerBankCode.setVersion(quicktellerBankCodeDTO.getVersion());
            quicktellerBankCode.setBankCodeId(quicktellerBankCodeDTO.getBankCodeId());
            quicktellerBankCode.setCbnCode(quicktellerBankCodeDTO.getCbnCode());
            quicktellerBankCode.setBankName(quicktellerBankCodeDTO.getBankName());
            quicktellerBankCode.setBankCode(quicktellerBankCodeDTO.getBankCode());
            this.quicktellerBankCodeRepo.save(quicktellerBankCode);
            logger.info("Quickteller BankCode {} updated", quicktellerBankCode.getBankName());
            return messageSource.getMessage("quickbankcode.update.success", null, locale);
        }

        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("quickbankcode.update.failure", null, locale));
        }

    }

    @Override
    public List<QuicktellerBankCodeDTO> getQuicktellerBankCodes() {
        Iterable<QuicktellerBankCode> fis = quicktellerBankCodeRepo.findAll();
        return convertEntitiesToDTOs(fis);
    }

    @Override
    public List<QuicktellerBankCodeDTO> getOtherLocalBanks() {
        List<QuicktellerBankCode>  fis = quicktellerBankCodeRepo.findAll();
        return convertEntitiesToDTOs(fis);
    }


    @Override
    public QuicktellerBankCodeDTO getQuicktellerBankCode(Long id) {
        return convertEntityToDTO(quicktellerBankCodeRepo.findById(id).get());
    }

    @Override
    public String deleteQuicktellerBankCode(Long id) throws InternetBankingException {
      try {
          QuicktellerBankCode finInst = quicktellerBankCodeRepo.findById(id).get();
          quicktellerBankCodeRepo.delete(finInst);
          logger.info("Quickteller BankCode  with Id {} deleted ", id.toString());
          return messageSource.getMessage("quickbankcode.delete.success", null, locale);
      }

      catch (InternetBankingException e){
          throw e;
      }
      catch (Exception e){
          throw new InternetBankingException(messageSource.getMessage("quickbankcode.delete.failure", null, locale));
      }
    }

    @Override
    public Page<QuicktellerBankCode> getQuicktellerBankCodes(Pageable pageDetails) {
        Page<QuicktellerBankCode> page = quicktellerBankCodeRepo.findAll(pageDetails);
//        List<QuicktellerBankCodeDTO> dtOs = convertEntitiesToDTOs(page.getContent());
//        long t = page.getTotalElements();

//        return new PageImpl<QuicktellerBankCodeDTO>(dtOs, pageDetails, t);
        return page;
    }



    @Override
    public QuicktellerBankCode getQuicktellerBankCodeByBankCode(String bankCode) {
        return quicktellerBankCodeRepo.findByBankCode(bankCode);
    }

    @Override
    public QuicktellerBankCode getQuicktellerBankCodeByBankCodeId(String bankCodeId) {
        return quicktellerBankCodeRepo.findByBankCodeId(bankCodeId);
    }

    @Override
    public QuicktellerBankCode getQuicktellerBankCodeByName(String bankName) {
        return quicktellerBankCodeRepo.findFirstByBankNameIgnoreCase(bankName);
    }

    @Override
	public Page<QuicktellerBankCode> findQuicktellerBankCodes(String pattern, Pageable pageDetails) {
//		 Page<QuicktellerBankCode> page = quicktellerBankCodeRepo.findUsingPattern(pattern,pageDetails);
//	        List<QuicktellerBankCodeDTO> dtOs = convertEntitiesToDTOs(page.getContent());
//	        long t = page.getTotalElements();

//	        return new PageImpl<QuicktellerBankCodeDTO>(dtOs, pageDetails, t);
        return quicktellerBankCodeRepo.findUsingPattern(pattern,pageDetails);
	}

    @Override
    public void refreshBankCodes() {
        logger.info("updating bank codes!!");
        List<QuicktellerBankCodeDTO> getBankCodes = integrationService.getBankCodes();
        List<QuicktellerBankCode> updatedBankCodes = compareAndUpdateBankCodes(getBankCodes);
        quicktellerBankCodeRepo.removeObsolete(updatedBankCodes.stream().map(QuicktellerBankCode::getId).collect(Collectors.toList()));
    }

    private List<QuicktellerBankCode> compareAndUpdateBankCodes(List<QuicktellerBankCodeDTO> updatedBankCodes){
        List<QuicktellerBankCode> bankCode = new ArrayList<>();
        updatedBankCodes.forEach(updatedBankCode -> {
            QuicktellerBankCode storedBankCode = quicktellerBankCodeRepo.findByBankCode(updatedBankCode.getBankCode());
            logger.info("STORED BANKCODES {}",storedBankCode);
            if (storedBankCode == null) {
                QuicktellerBankCode code = createBankCode(updatedBankCode);
                bankCode.add(code);
            } else {
                QuicktellerBankCode code = createBankCode(updatedBankCode);
                bankCode.add(code);
            }
        });
        return quicktellerBankCodeRepo.saveAll(bankCode);
    }

    private QuicktellerBankCode createBankCode(QuicktellerBankCodeDTO code){
        QuicktellerBankCode bankCode = new QuicktellerBankCode();
        bankCode.setBankCodeId(code.getBankCodeId());
        bankCode.setBankName(code.getBankName());
        bankCode.setCbnCode(code.getCbnCode());
        bankCode.setBankCode(code.getBankName());
        quicktellerBankCodeRepo.save(bankCode);
        return bankCode;
    }


}
