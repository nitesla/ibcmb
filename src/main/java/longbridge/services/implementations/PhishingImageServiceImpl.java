package longbridge.services.implementations;

import longbridge.models.PhishingImage;
import longbridge.repositories.PhishingImageRepo;
import longbridge.services.PhishingImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Created by Longbridge on 22/06/2017.
 */
@Service
public class PhishingImageServiceImpl implements PhishingImageService{

    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PhishingImageRepo phishingImageRepo;

    @Autowired
    MessageSource messageSource;

    private Locale locale = LocaleContextHolder.getLocale();

    @Override
    public String saveImage(PhishingImage phishingImage) {
         phishingImageRepo.save(phishingImage);
        logger.info("Added new image {} ");
        return messageSource.getMessage("phishingimage.add.success", null, locale);
    }

    @Override
    public String deleteImage(Long id) {
        return null;
    }


    @Override
    public Page<PhishingImage> getAllPhishingImages(Pageable pageDetails) {
        Page<PhishingImage> page = phishingImageRepo.findAll(pageDetails);
        List<PhishingImage> dtOs = page.getContent();
        long t = page.getTotalElements();
        Page<PhishingImage> pageImpl = new PageImpl<PhishingImage>(dtOs, pageDetails, t);
        return pageImpl;
    }

    @Override
    public List<PhishingImage> getPhishingImages() {
        return null;
    }
}
