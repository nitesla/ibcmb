package longbridge.repositories;

import longbridge.models.BillerCategory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BillerCategoryRepo  extends CommonRepo<BillerCategory, Long>{


    BillerCategory findByCategoryName(String categoryName);



    @Modifying
    @Query("update BillerCategory b set b.delFlag = 'Y' where b.id not in (:category) ")
    void removeObsolete(@Param("category") List<Long> collect);

    @Transactional
    @Modifying
    @Query("update BillerCategory item set item.enabled = :status where item.id = :id")
    void enableOrDisableCategory(@Param("id") Long id, Boolean status);
}
