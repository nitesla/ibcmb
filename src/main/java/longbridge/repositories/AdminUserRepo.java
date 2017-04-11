package longbridge.repositories;

import longbridge.dtos.AdminUserDTO;
import longbridge.models.AdminUser;
import longbridge.models.Permission;
import longbridge.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Repository

public interface AdminUserRepo extends CommonRepo<AdminUser, Long>{
    AdminUser findByUserName(String s);
}
