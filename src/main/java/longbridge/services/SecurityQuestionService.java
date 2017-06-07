package longbridge.services;

import longbridge.dtos.SecQuestionDTO;
import longbridge.models.SecurityQuestions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Showboy on 07/06/2017.
 */
public interface SecurityQuestionService {

    List<SecurityQuestions> getSecQuestions();

    SecurityQuestions getSecQuestion(Long id);

    Page<SecQuestionDTO> getSecQuestions(Pageable pageDetails);

    String addSecQuestion(String question);

    String updateSecQuestion(SecQuestionDTO secQuestionDTO);

    String deleteSecQuestion(Long id);

}
