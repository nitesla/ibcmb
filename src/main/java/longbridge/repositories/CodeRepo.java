package longbridge.repositories;

import longbridge.models.Code;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Wunmi on 30/03/2017.
 */
public interface CodeRepo extends JpaRepository<Code, Long>{

    Iterable<Code> findByTypeandDelFlag(String type, String delFlag);

    Iterable<Code> findByDelFlag(String delFlag);
}
