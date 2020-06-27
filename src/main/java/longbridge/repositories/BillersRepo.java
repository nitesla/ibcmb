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

@Repository
public interface BillersRepo extends CommonRepo<Biller, Long>{


    @Transactional
    @Modifying
    @Query("delete from Biller b where b.deleteValue = :deleteValue ")
    int deleteAllByDeleteValue(@Param("deleteValue") String deleteValue);

    List<Biller> findByCategoryName(String category);

    List<Biller> findByCategoryNameAndEnabled(String category, boolean enabled);

    Page<Biller> findByCategoryName(String categoryName, Pageable pageable);
    
//    @Query(
//  		  value = "select distinct category from Biller",
//  		  nativeQuery = true)
//    List<String> findAllCategories();
    

    

}
