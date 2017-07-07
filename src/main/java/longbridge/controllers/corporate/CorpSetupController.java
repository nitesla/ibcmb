package longbridge.controllers.corporate;

import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.PasswordStrengthDTO;
import longbridge.models.SecurityQuestions;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created by Showboy on 02/07/2017.
 */
@Controller
@RequestMapping("/corporate/setup")
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

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private CorporateUserService corporateUserService;

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


        logger.info("MASTER LIST {}", masterList);
        model.addAttribute("secQuestions", masterList);
        model.addAttribute("noOfQuestions", noOfQuestions);

        PasswordStrengthDTO passwordStrengthDTO = passwordPolicyService.getPasswordStengthParams();
        logger.info("Password Strength {}" + passwordStrengthDTO);
        model.addAttribute("passwordStrength", passwordStrengthDTO);

        return "corp/setup";
    }

    @PostMapping
    public @ResponseBody
    String saveSetUp(WebRequest webRequest, RedirectAttributes redirectAttributes, Principal principal){
        Iterator<String> iterator = webRequest.getParameterNames();

        while(iterator.hasNext()){
            logger.info(iterator.next());
        }

        String phishing = webRequest.getParameter("phishing");
        String caption = webRequest.getParameter("caption");
        String noOfQuestions = webRequest.getParameter("noOfQuestions");
        logger.info("no of questions {} ", noOfQuestions);

        List<String> secQuestions = new ArrayList<>();
        List<String> securityAnswers = new ArrayList<>();
        if(noOfQuestions != null){
            for(int i =0; i < Integer.parseInt(noOfQuestions); i++){
                secQuestions.add(webRequest.getParameter("securityQuestion"+i));
                securityAnswers.add(webRequest.getParameter("securityAnswer"+i));

                logger.info(" sec questions list {}",secQuestions);
                logger.info("sec answer list {}",securityAnswers);
            }
        }

        File image = new File(fullImagePath, phishing);
        Long length = image.length();
        // length <= Integer.MAX_VALUE;
        //TODO: check file is not bigger than max int
        byte buffer[] = new byte[length.intValue()];


        try {
            FileInputStream fis = new FileInputStream(image);
            int cnt = fis.read(buffer);
            //ensure cnt == length
        }catch (Exception e){
            //TODO: handle exception
        }

        String encPhishImage = java.util.Base64.getEncoder().encodeToString(buffer);
        logger.info("ENCODED STRING " + encPhishImage);

        CorporateUserDTO userDTO = corporateUserService.getUserDTOByName(principal.getName());
        userDTO.setSecurityQuestion(secQuestions);
        userDTO.setSecurityAnswer(securityAnswers);
        userDTO.setPhishingSec(encPhishImage);


        return "false";
    }

}
