package longbridge.services.implementations;

import longbridge.dtos.SecQuestionDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.SecurityQuestions;
import longbridge.repositories.SecQuestionRepo;
import longbridge.services.SecurityQuestionService;
import longbridge.utils.Verifiable;
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
 * Created by Wunmi on 07/06/2017.
 */
@Service
public class SecurityQuestionServiceImpl implements SecurityQuestionService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private SecQuestionRepo secQuestionRepo;

    @Autowired
    private MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<SecurityQuestions> getSecQuestions() {
        return secQuestionRepo.findAll();
    }

    @Override
    public SecurityQuestions getSecQuestion(Long id) {
        return secQuestionRepo.findById(id).get();
    }

    @Override

    public Page<SecQuestionDTO> getSecQuestions(Pageable pageDetails) {
        Page<SecurityQuestions> page = secQuestionRepo.findAll(pageDetails);
        List<SecQuestionDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        // return  new PageImpl<ServiceReqConfigDTO>(dtOs,pageDetails,page.getTotalElements());
        return new PageImpl<>(dtOs, pageDetails, t);
    }

    @Override
    @Verifiable(operation="ADD_SQ",description="Adding Security Question")
    public String addSecQuestion(String question)  {

        try {
            SecurityQuestions secQuestions = new SecurityQuestions();
            secQuestions.setSecurityQuestion(question);
            this.secQuestionRepo.save(secQuestions);
            logger.info("Security Question {} added", question);
            return messageSource.getMessage("secQues.add.success", null, locale);
        }
        catch (VerificationInterruptedException e) {
            return e.getMessage();
        }

        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("secQues.add.failure", null, locale));
        }
    }

    @Override
    @Verifiable(operation="UPDATE_SQ",description="Updating Security Question")
    public String updateSecQuestion(SecQuestionDTO secQuestionDTO) {
        try {
            SecurityQuestions securityQuestions = convertDTOToEntity(secQuestionDTO);
            this.secQuestionRepo.save(securityQuestions);
            logger.info("Security Question {} updated", securityQuestions);
            return messageSource.getMessage("secQues.update.success", null, locale);
        }
        catch (VerificationInterruptedException e) {
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("secQues.update.failure", null, locale));
        }
    }

    @Override
    @Verifiable(operation="DELETE_SQ",description="Deleting Security Question")
    public String deleteSecQuestion(Long id) {
        try{
            SecurityQuestions securityQuestions = secQuestionRepo.findById(id).get();
            this.secQuestionRepo.delete(securityQuestions);
            logger.info("Security Question {} deleted", securityQuestions);
            return messageSource.getMessage("secQues.delete.success", null, locale);
        }
        catch (VerificationInterruptedException e) {
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("secQues.delete.failure", null, locale));

        }
    }

    public List<SecQuestionDTO> convertEntitiesToDTOs(Iterable<SecurityQuestions> securityQuestions) {
        List<SecQuestionDTO> secQuestionDTOList = new ArrayList<>();
        for (SecurityQuestions securityQuestion : securityQuestions) {
            SecQuestionDTO secQuestionDTO = convertEntityToDTO(securityQuestion);
            secQuestionDTOList.add(secQuestionDTO);
        }
        return secQuestionDTOList;
    }

    public SecQuestionDTO convertEntityToDTO(SecurityQuestions securityQuestions) {
//        SecQuestionDTO secQuestionDTO = new SecQuestionDTO();
//        secQuestionDTO.setId(securityQuestions.getId());
//        secQuestionDTO.setSecQuestion(securityQuestions.getSecurityQuestion());
        return modelMapper.map(securityQuestions, SecQuestionDTO.class);
        //return  secQuestionDTO;
    }


    public SecurityQuestions convertDTOToEntity(SecQuestionDTO secQuestionDTO) {
        return modelMapper.map(secQuestionDTO, SecurityQuestions.class);
    }
}
