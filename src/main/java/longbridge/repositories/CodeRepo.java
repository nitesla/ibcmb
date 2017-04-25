package longbridge.repositories;

import longbridge.models.Code;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Wunmi on 30/03/2017.
 */
public interface CodeRepo extends CommonRepo<Code, Long>{


    Iterable<Code> findByType(String type);
    Code findByTypeAndCode(String type, String code);


    Code findByTypeAndCode(String type, String code);


}
