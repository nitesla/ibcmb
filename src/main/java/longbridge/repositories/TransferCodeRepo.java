package longbridge.repositories;

import longbridge.models.TransferCodeTransalator;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ayoade_farooq@yahoo.com on 7/16/2017.
 */
public interface TransferCodeRepo  extends JpaRepository<TransferCodeTransalator,Long>{

    TransferCodeTransalator findFirstByResponseCode(String s);
}
