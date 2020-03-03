package longbridge.repositories;

import longbridge.models.Merchant;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Yemi Dalley
 *
 */
@Repository
public interface MerchantRepo extends CommonRepo<Merchant, Long>{

    List<Merchant> findByCategory(String category);

    List<Merchant> findByCategoryAndEnabled(String category, boolean enabled);

    Page<Merchant> findByCategory(String category, Pageable pageable);
    
    @Query(
  		  value = "select distinct category from Merchant",
  		  nativeQuery = true)
    List<String> findAllCategories();
    
    @Modifying
    @Query("update Merchant m set m.enabled = :enabled  where m.id= :id")
    void setEnabledFlag(@Param("id") Long id, @Param("enabled") boolean enabled);
    
//    @Query(
//    		  value = "select distinct category from Merchant", 
//    		  countQuery = "SELECT count(distinct category) from Merchant", 
//    		  nativeQuery = true)
//    Page<String> findCategoriesOnly(Pageable pageable);
    
}
