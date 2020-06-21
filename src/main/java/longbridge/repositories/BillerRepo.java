package longbridge.repositories;


import longbridge.models.Billers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BillerRepo extends CommonRepo<Billers, Long>{


    @Transactional
    @Modifying
    @Query("delete from Billers b where b.deleteValue = :deleteValue ")
    int deleteAllByDeleteValue(@Param("deleteValue") String deleteValue);

    List<Billers> findByCategoryName(String category);

    List<Billers> findByCategoryNameAndEnabled(String category, boolean enabled);

    Page<Billers> findByCategoryName(String categoryName, Pageable pageable);
    
    @Query(
  		  value = "select distinct category from Biller",
  		  nativeQuery = true)
    List<String> findAllCategories();
    
    @Modifying
    @Query("update Billers m set m.enabled = :enabled  where m.id= :id")
    void setEnabledFlag(@Param("id") Long id, @Param("enabled") boolean enabled);
    
//
    

}
