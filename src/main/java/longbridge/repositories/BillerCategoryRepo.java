package longbridge.repositories;

import longbridge.models.BillerCategory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillerCategoryRepo  extends CommonRepo<BillerCategory, Long>{


    BillerCategory findByCategoryId(Long categoryId);



    @Modifying
    @Query("update BillerCategory b set b.delFlag = 'Y' where b.id not in (:category) ")
    void removeObsolete(@Param("category") List<Long> collect);
}
