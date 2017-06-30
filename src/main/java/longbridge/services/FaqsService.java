package longbridge.services;

import longbridge.dtos.FaqsDTO;
import longbridge.models.Faqs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Showboy on 24/06/2017.
 */
public interface FaqsService {

    List<FaqsDTO> getFaqs();

    Page<FaqsDTO> getFaqs(Pageable pageDetails);

    FaqsDTO getFaq(Long id);

    @PreAuthorize("hasAuthority('ADD_FAQ')")
    String addFaq(FaqsDTO faqsDTO);

    @PreAuthorize("hasAuthority('UPDATE_FAQ')")
    String updateFaq(FaqsDTO faqsDTO);

    @PreAuthorize("hasAuthority('DELETE_FAQ')")
    String deleteFaq(Long id);

    FaqsDTO convertEntityToDTO(Faqs faqs);

    Faqs convertDTOToEntity(FaqsDTO faqsDTO);

    List<FaqsDTO> convertEntitiesToDTOs(List<Faqs> faqs);
}
