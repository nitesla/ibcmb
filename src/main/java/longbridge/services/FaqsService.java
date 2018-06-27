package longbridge.services;

import longbridge.dtos.FaqsDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Faqs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by Wunmi on 24/06/2017.
 */
public interface FaqsService {

    List<FaqsDTO> getFaqs();

    Page<FaqsDTO> getFaqs(Pageable pageDetails);

    FaqsDTO getFaq(Long id);

    @PreAuthorize("hasAuthority('ADD_FAQ')")
    String addFaq(FaqsDTO faqsDTO) throws InternetBankingException;

    @PreAuthorize("hasAuthority('UPDATE_FAQ')")
    String updateFaq(FaqsDTO faqsDTO) throws InternetBankingException;

    @PreAuthorize("hasAuthority('DELETE_FAQ')")
    String deleteFaq(Long id) throws InternetBankingException;

    FaqsDTO convertEntityToDTO(Faqs faqs);

    Faqs convertDTOToEntity(FaqsDTO faqsDTO);

    List<FaqsDTO> convertEntitiesToDTOs(List<Faqs> faqs);
}
