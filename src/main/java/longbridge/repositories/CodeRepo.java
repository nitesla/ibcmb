package longbridge.repositories;

import longbridge.models.Code;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * Created by Wunmi on 30/03/2017.
 */
public interface CodeRepo extends CommonRepo<Code, Long>{

    List<Code> findByType(String type);
    Page<Code> findByType(String type,Pageable pageable);
    Code findByTypeAndCode(String type, String code);
    @Query("select distinct c.type from Code c")
    Iterable<String> findAllTypes();
    @Query("select distinct c.type from Code c")
    Page<String> findAllTypes(Pageable pageable);
    @Query("select distinct c from Code c where c.code=:code")
    Code getCode(@Param("code") String code);
    Code getCodeById(Long id);
    List<Code> findAllByType(String type);
    @Query("select distinct c.code from Code c where c.type=:type")
    Set<String> getCodeByType(@Param("type") String type);

    @Query("select distinct c.type from Code c where upper(c.type) like upper(concat('%', :pattern,'%')) ")
    Page<String> searchByType(@Param("pattern") String pattern, Pageable pageDetails);
}
