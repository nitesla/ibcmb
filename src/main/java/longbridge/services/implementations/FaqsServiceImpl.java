package longbridge.services.implementations;

import longbridge.dtos.FaqsDTO;
import longbridge.models.Faqs;
import longbridge.repositories.NotificationsRepo;
import longbridge.services.FaqsService;
import longbridge.utils.Verifiable;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    NotificationsRepo notificationsRepo;

    @Override
    public List<FaqsDTO> getFaqs() {
        return null;
    }

    @Override
    public FaqsDTO getFaq(Long id) {
        return null;
    }

    @Override
    @Verifiable(operation = "ADD_FAQ", description = "Adding FAQs")
    public String addFaq(FaqsDTO faqsDTO) {
        return null;
    }

    @Override
    @Verifiable(operation = "UPDATE_FAQ", description = "Updating an FAQs")
    public String updateFaq(FaqsDTO faqsDTO) {
        return null;
    }

    @Override
    @Verifiable(operation = "DELETE_FAQ", description = "Deleting FAQs")
    public String deleteFaq(Long id) {
        return null;
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
