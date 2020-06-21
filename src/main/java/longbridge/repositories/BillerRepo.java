package longbridge.repositories;

import longbridge.models.Biller;
import longbridge.models.Merchant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BillerRepo extends CommonRepo<Biller, Long>{

    List<Biller> findByCategory(String categoryName);

    List<Biller> findByCategoryAndEnabled(String categoryName, boolean enabled);

    Page<Biller> findByCategory(String categoryName, Pageable pageable);
    
    @Query(
  		  value = "select distinct categoryName from Biller",
  		  nativeQuery = true)
    List<String> findAllCategories();
    
    @Modifying
    @Query("update Biller m set m.enabled = :enabled  where m.id= :id")
    void setEnabledFlag(@Param("id") Long id, @Param("enabled") boolean enabled);
    
//
    
}
