package longbridge.services.implementations;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.CodeTypeDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.Code;
import longbridge.repositories.CodeRepo;
import longbridge.services.CodeService;
import longbridge.trace.Trace;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by Wunmi on 29/03/2017.
 */
@Service
public class CodeServiceImpl implements CodeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CodeRepo codeRepo;

     private final ModelMapper modelMapper;

    private final Locale locale = LocaleContextHolder.getLocale();


    @Autowired
    private MessageSource messageSource;

    @Autowired
    public CodeServiceImpl(CodeRepo codeRepository,  ModelMapper modelMapper) {
        codeRepo = codeRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    @Transactional
    @Verifiable(operation = "DELETE_CODE", description = "Deleting a Code")
    public String deleteCode(Long codeId)  {
        try {
            Code code = codeRepo.findById(codeId).get();
            if(code.getType().equals("ACCOUNT_COVERAGE")){

            }
            codeRepo.delete(code);
            logger.info("Code {} has been deleted", codeId.toString());
            return messageSource.getMessage("code.delete.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (InternetBankingException e) {
            throw e;
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("code.delete.failure", null, locale), e);
        }
    }

    @Override
    public CodeDTO getCode(Long id) {
        Code code = this.codeRepo.findById(id).get();
        return convertEntityToDTO(code);
    }

    @Override
    public Code getCodeById(Long id) {
        return this.codeRepo.findById(id).get();
    }

    @Override
    @Cacheable(value = "codes")
    public List<CodeDTO> getCodesByType(String codeType) {
        return codeRepo.findByType(codeType).stream()
                .map(this::convertEntityToDTO).collect(Collectors.toList());
    }



    @Transactional
    @CacheEvict(value = "codes", key = "#codeDTO.type")
    @Verifiable(operation = "UPDATE_CODE", description = "Updating a Code")
    public String updateCode(CodeDTO codeDTO)  {
        try {
            Code code = convertDTOToEntity(codeDTO);
            codeRepo.save(code);
            logger.info("Updated code with Id {}", code.getId());
            return messageSource.getMessage("code.update.success", null, locale);
        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("code.update.failure", null, locale), e);
        }


    }


    private CodeDTO convertEntityToDTO(Code code) {
        return modelMapper.map(code, CodeDTO.class);
    }


    private Code convertDTOToEntity(CodeDTO codeDTO) {
        return modelMapper.map(codeDTO, Code.class);
    }


    @Override
    public Page<CodeDTO> getCodesByType(String codeType, Pageable pageDetails) {
        return codeRepo.findByType(codeType, pageDetails).map(this::convertEntityToDTO);
    }

    @Override
    public Page<CodeDTO> getCodes(Pageable pageDetails) {
        return codeRepo.findAll(pageDetails).map(this::convertEntityToDTO);
    }


    @Override
    @Verifiable(operation = "ADD_CODE", description = "Adding a Code")
    @Trace("alter-user")
    @CacheEvict(value = "codes", key = "#codeDTO.type")
    public String addCode(CodeDTO codeDTO)  {
        try {
            Code code = convertDTOToEntity(codeDTO);
            codeRepo.save(code);
            logger.info("Added new code {} of type {}", code.getDescription(), code.getType());
            return messageSource.getMessage("code.add.success", null, locale);

        } catch (VerificationInterruptedException e) {
            return e.getMessage();
        } catch (Exception e) {
            throw new InternetBankingException(messageSource.getMessage("code.add.failure", null, locale), e);
        }
    }

    @Override

    public Code getByTypeAndCode(String type, String code) {
        return codeRepo.findByTypeAndCode(type, code);
    }


    @Override
    public Page<CodeTypeDTO> getCodeTypes(Pageable pageDetails) {

        Page<String> allTypes = codeRepo.findAllTypes(pageDetails);
        return wrapType(pageDetails, allTypes);

    }

    @Override
    public Page<CodeTypeDTO> getCodeTypes(String pattern, Pageable pageDetails) {
              Page<String> foundTypes = codeRepo.searchByType(pattern,pageDetails);
        return wrapType(pageDetails, foundTypes);
    }

    private Page<CodeTypeDTO> wrapType(Pageable pageDetails, Page<String> allTypes) {
        long t = allTypes.getTotalElements();
        List<CodeTypeDTO> list = new ArrayList<>();
        for (String s : allTypes) {
            list.add(new CodeTypeDTO(s));
        }
        return new PageImpl<>(list, pageDetails, t);
    }
}