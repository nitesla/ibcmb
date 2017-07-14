package longbridge.services.implementations;

import longbridge.dtos.FaqsDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.VerificationInterruptedException;
import longbridge.models.Faqs;
import longbridge.repositories.FaqsRepo;
import longbridge.services.FaqsService;
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
 * Created by Showboy on 24/06/2017.
 */
@Service
public class FaqsServiceImpl implements FaqsService {

    @Autowired
    private ModelMapper modelMapper;

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    MessageSource messageSource;

    @Autowired
    FaqsRepo faqsRepo;

    @Override
    public List<FaqsDTO> getFaqs() {
        List<Faqs> faqs = this.faqsRepo.findAll();
        return convertEntitiesToDTOs(faqs);
    }

    @Override
    public Page<FaqsDTO> getFaqs(Pageable pageDetails) {
        Page<Faqs> page = faqsRepo.findAll(pageDetails);
        List<FaqsDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        Page<FaqsDTO> pageImpl = new PageImpl<FaqsDTO>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public FaqsDTO getFaq(Long id) {
        Faqs faqs = this.faqsRepo.findOne(id);
        return convertEntityToDTO(faqs);
    }

    @Override
    @Verifiable(operation = "ADD_FAQ", description = "Adding FAQs")
    public String addFaq(FaqsDTO faqsDTO) throws InternetBankingException {
        try {
            Faqs faqs = convertDTOToEntity(faqsDTO);
            faqsRepo.save(faqs);
            logger.info("Added new Notification {} ", faqs.getQuestion());
            return messageSource.getMessage("faq.add.success", null, locale);
        }

        catch (VerificationInterruptedException e){
            return e.getMessage();
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("failure",null,locale));
        }
    }

    @Override
    @Verifiable(operation = "UPDATE_FAQ", description = "Updating an FAQs")
    public String updateFaq(FaqsDTO faqsDTO) throws InternetBankingException {
        try {
            Faqs faqs = convertDTOToEntity(faqsDTO);
            faqsRepo.save(faqs);
            logger.info("Updated Notification with Id {}", faqs.getQuestion());
            return messageSource.getMessage("faq.update.success", null, locale);
        }
        catch (VerificationInterruptedException e){
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("failure",null,locale));
        }
    }

    @Override
    @Verifiable(operation = "DELETE_FAQ", description = "Deleting FAQs")
    public String deleteFaq(Long id) throws InternetBankingException {
        try{
            Faqs faqs = faqsRepo.findOne(id);
            faqsRepo.delete(faqs);
            logger.info("Notification {} has been deleted",id.toString());
            return messageSource.getMessage("faq.delete.success",null,locale);
        }
        catch (VerificationInterruptedException e){
            return e.getMessage();
        }
        catch (InternetBankingException e){
            throw e;
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("failure",null,locale));
        }
    }

    @Override
    public FaqsDTO convertEntityToDTO(Faqs faqs) {
        return  modelMapper.map(faqs,FaqsDTO.class);
    }

    @Override
    public Faqs convertDTOToEntity(FaqsDTO faqsDTO) {
        return  modelMapper.map(faqsDTO,Faqs.class);
    }

    @Override
    public List<FaqsDTO> convertEntitiesToDTOs(List<Faqs> faqs) {
        List<FaqsDTO> faqsDTOList = new ArrayList<>();
        for(Faqs faq: faqs){
            FaqsDTO faqsDTO = convertEntityToDTO(faq);
            faqsDTOList.add(faqsDTO);
        }
        return faqsDTOList;
    }
}
