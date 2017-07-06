package longbridge.controllers.corporate;

import longbridge.models.SecurityQuestions;
import longbridge.services.MessageService;
import longbridge.services.SecurityQuestionService;
import longbridge.services.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Showboy on 02/07/2017.
 */
@Controller
@RequestMapping("/corporate")
public class CorpSetupController {

    private Locale locale = LocaleContextHolder.getLocale();

    private Logger logger= LoggerFactory.getLogger(this.getClass());

    @Value("${antiphishingimagepath}")
    private String imagePath;

    @Value("${phishing.image.folder}")
    private String fullImagePath;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SecurityQuestionService securityQuestionService;

    @Autowired
    private SecurityService securityService;

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

    @GetMapping
    public String setUp(Model model){

        File phish = new File(fullImagePath);
        List<String> images = new ArrayList<String>();
        if (phish.isDirectory()) { // make sure it's a directory
            for (final File f : phish.listFiles(IMAGE_FILTER)) {
                images.add(f.getName());
                logger.info("FILE NAME {}", f.getName());
            }
        }

        model.addAttribute("images", images);
        model.addAttribute("imagePath", imagePath);

        List<SecurityQuestions> secQues = securityQuestionService.getSecQuestions();
        int noOfQuestions = securityService.getMinUserQA();
        logger.info("num of qs on entrust {}",noOfQuestions);
        ArrayList[] masterList = new ArrayList[noOfQuestions];
        int questionsPerSection = (secQues.size()-(secQues.size()%noOfQuestions))/noOfQuestions;
        logger.info("question per section {}",questionsPerSection);
        int questnPostn = 0;
        for(int i=0;i< noOfQuestions;i++) {
            masterList[i] =  new ArrayList<>();
            for (int j = 0; j <questionsPerSection; j++) {
                masterList[i].add(secQues.get(questnPostn));
                questnPostn++;
            }

        }
        logger.info("master question length"+masterList.length);

        for (int i=0;i<masterList.length;i++  ) {
            logger.info("master question "+i+" "+masterList[i]);
        }
        logger.info("master question "+masterList);

        model.addAttribute("secQuestions", secQues);

        return "corp/setup";
    }

}
