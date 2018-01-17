package longbridge.repositories;

import longbridge.models.Code;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Wunmi on 30/03/2017.
 */
public interface CodeRepo extends CommonRepo<Code, Long>{

    Iterable<Code> findByType(String type);
    Page<Code> findByType(String type,Pageable pageable);
    Code findByTypeAndCode(String type, String code);
    @Query("select distinct c.type from Code c")
    Iterable<String> findAllTypes();
    @Query("select distinct c.type from Code c")
    Page<String> findAllTypes(Pageable pageable);
    
    
    
}
