package longbridge.repositories;

import longbridge.models.SecurityQuestions;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by Showboy on 07/06/2017.
 */
@Repository
@Transactional
public interface SecQuestionRepo extends CommonRepo<SecurityQuestions, Long>{


}
