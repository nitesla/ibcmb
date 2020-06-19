package longbridge.repositories;

import longbridge.models.Corporate;
import longbridge.models.CorporateRole;
import longbridge.models.CorporateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Fortune on 6/7/2017.
 */
public interface CorporateRoleRepo extends CommonRepo<CorporateRole,Long> {


    Page<CorporateRole> findByCorporate(Corporate corporate, Pageable pageable);

    List<CorporateRole> findByCorporate(Corporate corporate);

    CorporateRole findFirstByNameAndRankAndCorporate_Id(String name, Integer rank, Long corpId);

//    @Query("select count(cr) > 0 from CorporateRole cr where :user member of cr.users and cr=:role")
//    boolean existInRole(@Param("role") CorporateRole role, @Param("user") CorporateUser user);

    @Query("select count(cr) from CorporateRole cr where :user member of cr.users and cr=:role")
    Integer countInRole(@Param("role") CorporateRole role, @Param("user") CorporateUser user);


}
