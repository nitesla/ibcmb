package longbridge.repositories;


import longbridge.models.Biller;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.LongStream;

@Repository
public interface BillerRepo extends CommonRepo<Biller, Long>{

    Biller findByBillerId(Long billerId);

    List<Biller> findByBillerIdNotIn(List<Long> billerIds);

    List<Biller> findByCategoryName(String category);

    List<Biller> findByCategoryNameAndEnabled(String category, boolean enabled);

    Page<Biller> findByCategoryName(String categoryName, Pageable pageable);
    
    @Query(
  		  value = "select distinct category from Biller",
  		  nativeQuery = true)
    List<String> findAllCategories();
    
    @Modifying
    @Query("update Biller m set m.enabled = :enabled  where m.id= :id")
    void setEnabledFlag(@Param("id") Long id, @Param("enabled") boolean enabled);


    @Modifying
    @Query("update Biller b set b.delFlag = 'Y' where b.id not in (:billers) ")
    void removeObsolete(@Param("billers") List<Long> validBillers);

    @Transactional
    @Modifying
    @Query("update Biller b set b.enabled = :status where b.id = :id")
    void enableOrDisableBiller(@Param("id") Long id, Boolean status);


//
    

}
