package longbridge.services;

import longbridge.dtos.SecQuestionDTO;
import longbridge.models.SecurityQuestions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by Showboy on 07/06/2017.
 */
public interface SecurityQuestionService {


    List<SecurityQuestions> getSecQuestions();

    SecurityQuestions getSecQuestion(Long id);

    Page<SecQuestionDTO> getSecQuestions(Pageable pageDetails);

    @PreAuthorize("hasAuthority('ADD_SQ')")
    String addSecQuestion(String question);

    @PreAuthorize("hasAuthority('UPDATE_SQ')")
    String updateSecQuestion(SecQuestionDTO secQuestionDTO);

    @PreAuthorize("hasAuthority('DELETE_SQ')")
    String deleteSecQuestion(Long id);

}
