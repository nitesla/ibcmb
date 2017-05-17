package longbridge.services.implementations;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.CodeTypeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.AdminUser;
import longbridge.models.Code;
import longbridge.repositories.CodeRepo;
import longbridge.services.CodeService;
import longbridge.services.VerificationService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class CodeServiceImpl implements CodeService {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private CodeRepo codeRepo;

    private VerificationService verificationService;

    private ModelMapper modelMapper;

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    MessageSource messageSource;

    @Autowired
    public CodeServiceImpl(CodeRepo codeRepository, VerificationService verificationService, ModelMapper modelMapper) {
        codeRepo = codeRepository;
        this.verificationService = verificationService;
        this.modelMapper = modelMapper;
    }


    @Override
    @Transactional
    public String deleteCode(Long codeId) throws InternetBankingException{
          try{

           codeRepo.delete(codeId);
           logger.info("Code {} has been deleted",codeId.toString());
           return messageSource.getMessage("code.delete.success",null,locale);
    }
    catch (Exception e){
          throw new InternetBankingException(messageSource.getMessage("code.delete.failure",null,locale));
          }
    }

    @Override
    public CodeDTO getCode(Long id) {
        Code code = this.codeRepo.findOne(id);
        return convertEntityToDTO(code);
    }

    @Override
    public Code getCodeById(Long id) {
        Code code = this.codeRepo.findOne(id);
        return code;
    }

    @Override
    public List<CodeDTO> getCodesByType(String codeType) {
        Iterable<Code> codes = this.codeRepo.findByType(codeType);
        return convertEntitiesToDTOs(codes);
    }

    @Override
    public Iterable<CodeDTO> getCodes() {
        Iterable<Code> codes = this.codeRepo.findAll();
        return convertEntitiesToDTOs(codes);    }

  

    @Transactional
    public String updateCode(CodeDTO codeDTO, AdminUser adminUser) throws InternetBankingException{
        try {
            Code code = convertDTOToEntity(codeDTO);
            // Code originalObject = codeRepo.findOne(code.getId());
            //check if maker checker is enabled
            codeRepo.save(code);
            logger.info("Updated code with Id {}",code.getId());
            return messageSource.getMessage("code.update.success", null, locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("code.update.failure",null,locale));
        }

        
    }

  
    public CodeDTO convertEntityToDTO(Code code){
        return  modelMapper.map(code,CodeDTO.class);
    }


    public Code convertDTOToEntity(CodeDTO codeDTO){
        return  modelMapper.map(codeDTO,Code.class);
    }

    public List<CodeDTO> convertEntitiesToDTOs(Iterable<Code> codes){
        List<CodeDTO> codeDTOList = new ArrayList<>();
        for(Code code: codes){
            CodeDTO codeDTO = convertEntityToDTO(code);
            codeDTOList.add(codeDTO);
        }
        return codeDTOList;
    }



	@Override
	public Page<CodeDTO> getCodesByType(String codeType, Pageable pageDetails) {
		// TODO Auto-generated method stub
		Page<Code> page = codeRepo.findByType(codeType, pageDetails);
		List<CodeDTO> dtOs = convertEntitiesToDTOs(page);
		long t = page.getTotalElements();
        Page<CodeDTO> pageImpl = new PageImpl<CodeDTO>(dtOs,pageDetails,t);
        return pageImpl;
	}

    @Override
    public Page<CodeDTO> getCodes(Pageable pageDetails) {
        Page<Code> page = codeRepo.findAll(pageDetails);
        List<CodeDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        Page<CodeDTO> pageImpl = new PageImpl<CodeDTO>(dtOs,pageDetails,t);
        return pageImpl;
    }


	@Override
	public String addCode(CodeDTO codeDTO, AdminUser adminUser) throws InternetBankingException {
		try {
            Code code = convertDTOToEntity(codeDTO);
            //check if maker checker is enabled
            codeRepo.save(code);
            logger.info("Added new code {} of type {}",code.getDescription(),code.getType());
            return messageSource.getMessage("code.add.success", null, locale);
        }catch (Exception e){
		    throw new InternetBankingException(messageSource.getMessage("code.add.failure",null,locale));
        }

//        try {
//			return verificationService.addNewVerificationRequest(code);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			logger.error("Error", e);
//			return "Error adding Code " + e.getMessage();
//		}
	}

    @Override
    public Code getByTypeAndCode(String type, String code) {
        return codeRepo.findByTypeAndCode(type,code);
    }


	@Override
	public Page<CodeTypeDTO> getCodeTypes(Pageable pageDetails) {

		Page<String> allTypes = codeRepo.findAllTypes(pageDetails);
		long t = allTypes.getTotalElements();
		List<CodeTypeDTO> list = new ArrayList<CodeTypeDTO>();
		for(String s :allTypes){
			list.add(new CodeTypeDTO(s));
		}
		return new PageImpl<CodeTypeDTO>(list,pageDetails,t);
		
	}
}