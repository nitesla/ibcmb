package longbridge.services;

import longbridge.models.PhishingImage;

/**
 * Created by Longbridge on 22/06/2017.
 */
public interface PhishingImageService {

    String saveImage(PhishingImage phishingImage);

    String deleteImage(Long id);
}
