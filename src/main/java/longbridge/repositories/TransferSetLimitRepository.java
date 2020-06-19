package longbridge.repositories;

import longbridge.models.TransferSetLimit;
//<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;
//=======
//>>>>>>> 598cd74a9ed9e8e9986fe283f260980c47418ef5
import org.springframework.stereotype.Repository;

@Repository
public interface TransferSetLimitRepository extends CommonRepo<TransferSetLimit , Long> {
}
