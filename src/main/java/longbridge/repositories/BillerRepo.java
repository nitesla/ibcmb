package longbridge.repositories;

import longbridge.billerresponse.Biller;
import longbridge.models.Billers;
import org.springframework.data.domain.Page;
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
}
