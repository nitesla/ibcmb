package longbridge.controllers.corporate;

import longbridge.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Showboy on 02/07/2017.
 */
@Controller
@RequestMapping("/corporate")
public class CorpSetupController {

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MessageService messageService;

    @GetMapping
    public String setUp(){
        return "corp/setup/sec";
    }


    // array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
            "jpeg", "jpg", "gif", "png", "bmp" // and other formats you need
    };
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

}
