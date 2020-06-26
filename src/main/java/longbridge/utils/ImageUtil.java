package longbridge.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public class ImageUtil {


    @Value("${phishing.image.folder}")
    private String fullImagePath;

    public static String phisingImage (String imagePath, String phisingImageName){

        Logger logger = LoggerFactory.getLogger(new ImageUtil().getClass());

        File image = new File(imagePath, phisingImageName);
        Long length = image.length();
        // length <= Integer.MAX_VALUE;
        //TODO: check file is not bigger than max int
        byte buffer[] = new byte[length.intValue()];


        try {
            String encPhishImage = java.util.Base64.getEncoder().encodeToString(buffer);
            logger.info("ENCODED STRING " + encPhishImage);
            return  encPhishImage;
        } catch (Exception e) {
            logger.info("Phising image error {} ", e);
        }
       return null;
    }
}
