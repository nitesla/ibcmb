package longbridge.services;

import longbridge.dtos.FaqsDTO;
import longbridge.models.Faqs;

import java.util.List;

/**
 * Created by Showboy on 24/06/2017.
 */
public interface FaqsService {

    List<FaqsDTO> getFaqs();

    FaqsDTO getFaq(Long id);

    String addFaq(FaqsDTO faqsDTO);

    String updateFaq(FaqsDTO faqsDTO);

    String deleteFaq(Long id);

    FaqsDTO convertEntityToDTO(Faqs faqs);

    Faqs convertDTOToEntity(FaqsDTO faqsDTO);

    List<FaqsDTO> convertEntitiesToDTOs(List<Faqs> faqs);
}
