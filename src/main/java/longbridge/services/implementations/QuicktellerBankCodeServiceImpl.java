package longbridge.services.implementations;

import longbridge.dtos.QuicktellerBankCodeDTO;
import longbridge.exception.DuplicateObjectException;
import longbridge.exception.InternetBankingException;
import longbridge.models.QuicktellerBankCode;
import longbridge.repositories.QuicktellerBankCodeRepo;
import longbridge.services.QuicktellerBankCodeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        quicktellerBankCodeDTO.setId(quicktellerBankCode.getId());
        quicktellerBankCodeDTO.setVersion(quicktellerBankCode.getVersion());
        quicktellerBankCode.setBankCodeId(quicktellerBankCodeDTO.getBankCodeId());
        quicktellerBankCode.setCbnCode(quicktellerBankCodeDTO.getCbnCode());
        quicktellerBankCode.setBankName(quicktellerBankCodeDTO.getBankName());
        quicktellerBankCode.setBankCode(quicktellerBankCodeDTO.getBankCode());
        return quicktellerBankCodeDTO;
    }

    @Override
    public QuicktellerBankCode convertDTOToEntity(QuicktellerBankCodeDTO quicktellerBankCodeDTO) {
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
    public Page<QuicktellerBankCodeDTO> getQuicktellerBankCodes(Pageable pageDetails) {
        Page<QuicktellerBankCode> page = quicktellerBankCodeRepo.findAll(pageDetails);
        List<QuicktellerBankCodeDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        return new PageImpl<QuicktellerBankCodeDTO>(dtOs, pageDetails, t);
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
	public Page<QuicktellerBankCodeDTO> findQuicktellerBankCodes(String pattern, Pageable pageDetails) {
		 Page<QuicktellerBankCode> page = quicktellerBankCodeRepo.findUsingPattern(pattern,pageDetails);
	        List<QuicktellerBankCodeDTO> dtOs = convertEntitiesToDTOs(page.getContent());
	        long t = page.getTotalElements();

	        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        return new PageImpl<QuicktellerBankCodeDTO>(dtOs, pageDetails, t);
	}


}
