package longbridge.services;

import longbridge.dtos.PhishingImageDTO;
import longbridge.models.PhishingImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Longbridge on 22/06/2017.
 */
public interface PhishingImageService {

    String saveImage(PhishingImage phishingImage);

    String deleteImage(Long id);

    List<PhishingImage> getRandomPhishingImages();

    List<PhishingImage> getPhishingImages();

    Page<PhishingImageDTO> getAllPhishingImages(Pageable pageable);
}
