package longbridge.repositories;

import longbridge.models.Permission;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */

@Repository
public interface PermissionRepo extends CommonRepo<Permission, Long> {

    Iterable<Permission> findByIdNotIn(Long[] permissions);

    Iterable<Permission> findByUserType(String type);
    List<Permission> findByCategory(String category);
    Permission findByCode(String code);



//    @Query( "select v from permission v where v.initiatedBy != :initiated and v.operation in :permissionlist")
//    Page<Verification> findPermissionForUser(@Param("initiated") String initiatedBy, @Param("permissionlist") List<String> operation);



}
