package longbridge.repositories;

import longbridge.models.AdminPassword;
import longbridge.models.AdminUser;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Fortune on 6/5/2017.
 */
@Repository
public interface AdminPasswordRepo extends CommonRepo<AdminPassword,Long> {

    List<AdminPassword> findByUserId(Long userId);

//    List<AdminPassword> findByUserIdOrderByIdDesc(Long userId, Pageable pageable);

    int countByUserId(Long userId);

    AdminPassword findFirstByUserId(Long userId);

}
