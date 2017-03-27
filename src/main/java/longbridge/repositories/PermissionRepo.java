package longbridge.repositories;

import longbridge.models.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Showboy on 27/03/2017.
 */
public interface PermissionRepo extends JpaRepository<Permission, Long> {
}
