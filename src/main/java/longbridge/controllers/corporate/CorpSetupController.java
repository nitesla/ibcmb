package longbridge.controllers.corporate;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.CorporateUserDTO;
import longbridge.dtos.PasswordStrengthDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.forms.CustResetPassword;
import longbridge.models.CorporateUser;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.security.Principal;
import java.util.*;

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
    MessageSource messageSource;

    @Autowired
    private CodeService codeService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PasswordPolicyService passwordPolicyService;

    @Autowired
    private CorporateUserService corporateUserService;

    @Autowired
    private CorpProfileUpdateService corpProfileUpdateService;

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

        List<CodeDTO> secQues = codeService.getCodesByType("SECURITY_QUESTION");
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
        logger.trace("master question length"+masterList.length);

        for (int i=0;i<masterList.length;i++  ) {
            logger.trace("master question "+i+" "+masterList[i]);
        }
        logger.trace("master question "+ Arrays.toString(masterList));


        logger.trace("MASTER LIST {}", masterList);
        model.addAttribute("secQuestions", masterList);
        model.addAttribute("noOfQuestions", noOfQuestions);

        List<String> policies = passwordPolicyService.getPasswordRules();
        model.addAttribute("policies", policies);

        PasswordStrengthDTO passwordStrengthDTO = passwordPolicyService.getPasswordStrengthParams();
        logger.info("Password Strength {}" + passwordStrengthDTO);
        model.addAttribute("passwordStrength", passwordStrengthDTO);

        return "corp/setup";
    }

    @GetMapping("/tokenAuth/{token}")
    public @ResponseBody String tokenAth(@PathVariable String token, Principal principal){

        try {
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            boolean message = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), token);
            if (message){
                return "true";
            }
            return messageSource.getMessage("token.auth.failed", null, locale);
        }catch (InternetBankingException e){
            logger.error("ERROR AUTHENTICATING USER >>>>> ",e);
            return messageSource.getMessage("token.auth.failed", null, locale);
        }
    }

    @PostMapping
    public @ResponseBody
    String saveSetUp(WebRequest webRequest, RedirectAttributes redirectAttributes, Principal principal){
        Iterator<String> iterator = webRequest.getParameterNames();
logger.info("posting response ");
        String result= "";

        try {

            while(iterator.hasNext()){
                logger.info(iterator.next());
            }

            String phishing = webRequest.getParameter("phishing");
            String caption = webRequest.getParameter("caption");
            String noOfQuestions = webRequest.getParameter("noOfQuestions");
            String password = webRequest.getParameter("password");
            String confirm = webRequest.getParameter("confirm");
            String token = webRequest.getParameter("token");
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
            userDTO.setCaptionSec(caption);


            CustResetPassword custResetPassword = new CustResetPassword();
            custResetPassword.setNewPassword(password);
            custResetPassword.setConfirmPassword(confirm);


//            boolean valid = securityService.performTokenValidation(userDTO.getEntrustId(), userDTO.getEntrustGroup(), token);
//            if (valid){
                String message = corpProfileUpdateService.completeEntrustProfileCreation(userDTO);
                logger.info("MESSAGE", message);
                CorporateUser user = corporateUserService.getUserByName(userDTO.getUserName());
                String message2 = corporateUserService.resetPassword(user, custResetPassword);
                logger.info("MESSAGE 2", message2);
                return "true";
//            }else {
//                return messageSource.getMessage("token.auth.failure", null, locale);
//            }

        }catch (InternetBankingSecurityException ibe){
            logger.error("Error validating token", ibe);
            return ibe.getMessage();
        }catch (Exception e){
            logger.error("Error during corporate user setup", e);
            redirectAttributes.addFlashAttribute(messageSource.getMessage("user.add.failure", null, locale));
            return e.getMessage();
        }
    }
}
