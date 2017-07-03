package longbridge.repositories;

import longbridge.models.PhishingImage;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Longbridge on 22/06/2017.
 */
@Repository
public interface PhishingImageRepo extends CommonRepo<PhishingImage, Long>{

    @Query(nativeQuery=true, value="SELECT imagePath  FROM question ORDER BY random() LIMIT 10")
    List<PhishingImage> findImagePath();
}
