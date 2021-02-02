package longbridge.controllers;

import longbridge.dtos.FaqsDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingException;
import longbridge.exception.PasswordException;
import longbridge.exception.UnknownResourceException;
import longbridge.models.CorporateUser;
import longbridge.models.Email;
import longbridge.models.RetailUser;
import longbridge.services.*;
import longbridge.utils.CookieUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Controller
public class MainController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private RetailUserService retailUserService;
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private OperationsUserService opsUserService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CorporateUserService corporateUserService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private SettingsService configurationService;
    @Autowired
    private MailService mailService;
    @Autowired
    private FaqsService faqsService;
    @Autowired
    private LoggedUserService loggedUserService;
    private Integer sessionTimeout = 5 * 60 ;

    @Value("${feedback.url}")
    private String feedbackUrl;

    @PostConstruct
    void computeSessionTimeout(){
        SettingDTO settingDTO = configurationService.getSettingByName("SESSION_TIMEOUT");
        if(settingDTO != null && settingDTO.isEnabled()){
            String ts = settingDTO.getValue();
            try {
                sessionTimeout = NumberUtils.createInteger(ts) * 60;
            }catch (Exception e){
                logger.info("Cannot read SESSION_TIMEOUT, Using deafault");
            }
        }
    }

    @RequestMapping(value = {"/", "/home"})
    public String getHomePage(HttpSession session, HttpServletResponse response) {

        session.invalidate();
        SecurityContextHolder.clearContext();
	   Cookie cookie = new Cookie ( "time_out_time",sessionTimeout.toString());
	   cookie.setMaxAge ( 10000 );
	   response.addCookie ( cookie );
        return "index";
    }

    @RequestMapping(value = "/login/retail", method = RequestMethod.GET)
    public ModelAndView getLoginPage(@RequestParam Optional<String> error, @RequestParam Optional<HttpServletRequest> request, Model model,HttpServletResponse response,HttpServletRequest requests,HttpSession session) {

        request.ifPresent(httpServletRequest -> httpServletRequest.getSession().invalidate());
        //clearSession();
        SecurityContextHolder.clearContext();
        Cookie cookie = CookieUtil.getCookie(requests);
        cookie.setValue("" + sessionTimeout);
        cookie.setPath("/");
        response.addCookie(cookie);
        return new ModelAndView("retpage1", "error", error);
    }

    @RequestMapping(value = "/login/retail/feedback", method = RequestMethod.GET)
    public ModelAndView getFeedbackPage(@RequestParam Optional<String> error, @RequestParam Optional<HttpServletRequest> request, Model model,HttpServletResponse response,HttpServletRequest requests,HttpSession session) {

        request.ifPresent(httpServletRequest -> httpServletRequest.getSession().invalidate());
        //clearSession();
        SecurityContextHolder.clearContext();
        Cookie cookie = CookieUtil.getCookie(requests);
        cookie.setValue("" + sessionTimeout);
        cookie.setPath("/");
        response.addCookie(cookie);
//        return new ModelAndView("retpagefeedback", "error", error);
        return  new ModelAndView("redirect:" + feedbackUrl);
    }

    @RequestMapping(value = "/login/corporate", method = RequestMethod.GET)
    public ModelAndView getCorpLoginPage(@RequestParam Optional<String> error, @RequestParam Optional<HttpServletRequest> request,HttpServletResponse response,HttpServletRequest requests) {
//        SecurityContextHolder.clearContext();
        Cookie cookie = CookieUtil.getCookie(requests);
        cookie.setValue(sessionTimeout.toString());
        cookie.setPath("/");
        response.addCookie(cookie);

        request.ifPresent(httpServletRequest -> httpServletRequest.getSession().invalidate());
        //clearSession();
        return new ModelAndView("corppage1", "error", error);

    }

    @RequestMapping(value = "/login/corporate/feedback", method = RequestMethod.GET)
    public ModelAndView getCorpFeedbackPage(@RequestParam Optional<String> error, @RequestParam Optional<HttpServletRequest> request,HttpServletResponse response,HttpServletRequest requests) {
//        SecurityContextHolder.clearContext();
        Cookie cookie = CookieUtil.getCookie(requests);
        cookie.setValue(sessionTimeout.toString());
        cookie.setPath("/");
        response.addCookie(cookie);

        request.ifPresent(httpServletRequest -> httpServletRequest.getSession().invalidate());
        //clearSession();
//        return new ModelAndView("corppagefeedback", "error", error);
        return  new ModelAndView("redirect:" + feedbackUrl);

    }

    @GetMapping(value = "/login/admin")
    public ModelAndView adminLogin( HttpServletResponse response,HttpServletRequest requests) {
        Cookie cookie = CookieUtil.getCookie(requests);
        cookie.setValue(sessionTimeout.toString());
        cookie.setPath("/");
        response.addCookie(cookie);
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("admlogin");
        return modelAndView;
    }

    @GetMapping(value = "/login/ops")
    public ModelAndView opsLogin(HttpServletResponse response,HttpServletRequest requests) {
        Cookie cookie = CookieUtil.getCookie(requests);
        cookie.setValue(sessionTimeout.toString());
        cookie.setPath("/");
        response.addCookie(cookie);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("opslogin");

        return modelAndView;
    }


    @GetMapping("/faqs")
    public String viewFAQs(Model model) {
        List<FaqsDTO> faqs = faqsService.getFaqs();
        logger.info("FAQS {}", faqs);
        model.addAttribute("faqList", faqs);
        return "faqs";
    }

    @GetMapping("/login/retail/failure")
    public String retLoginFailure() {

        return "retloginfailure";
    }

    @GetMapping("/login/corporate/failure")
    public String corpLoginFailure() {

        return "corploginfailure";
    }


    @GetMapping(value = {"/retail/{path:(?!static).*$}", "/retail/{path:(?!static).*$}/**"})
    public String retailUnknown(Principal principal) {
        if (principal != null) {
            return "redirect:/retail/dashboard";
        }

        throw new UnknownResourceException();
        //   return "";
    }


    @GetMapping(value = {"/admin/{path:(?!static).*$}", "/admin/{path:(?!static).*$}/**"})
    public String adminUnknown(Principal principal) {
        if (principal != null) {
            return "redirect:/admin/dashboard";
        }
        throw new UnknownResourceException();
        // return "";
    }

    @GetMapping(value = {"/ops/{path:(?!static).*$}", "/ops/{path:(?!static).*$}/**"})
    public String opsUnknown(Principal principal) {
        if (principal != null) {
            return "redirect:/ops/dashboard";
        }
        throw new UnknownResourceException();
        // return "";
    }

    @GetMapping("/login/u/retail")
    public String login1() {
        return "redirect:/login/retail";
    }

    @PostMapping("/login/u/retail")
    public String userExists(WebRequest webRequest, Model model, HttpSession session) {
        String username = webRequest.getParameter("username");
        if (username == null) {
            return "retpage1";
        }

        RetailUser user = retailUserService.getUserByName(username);
        if (user == null) {
            return "redirect:/login/retail/failure";
        }

        try {
            Map<String, List<String>> mutualAuth = securityService.getMutualAuth(user.getEntrustId(), user.getEntrustGroup());
            if (mutualAuth != null) {
                String image = mutualAuth.get("imageSecret")
                        .stream()
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse("");

//                logger.info("SECIMAGE"+ image);

                String caption = mutualAuth.get("captionSecret")
                        .stream()
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse("");

                model.addAttribute("secImage", image);
                //logger.info("SECCAPTION "+ caption);
                model.addAttribute("secCaption", caption);
            }
        } catch (InternetBankingException e) {
            model.addAttribute("imageException", "You are yet to set your antiphishing image");
        }

        model.addAttribute("username", user.getUserName());
        session.removeAttribute("username");
        session.setAttribute("username", user.getUserName());
        return "retpage2";
    }

    @GetMapping("/login/p/retail")
    public String login2() {
        return "redirect:/login/retail";
    }


    //by gb
   /* @PostMapping("/login/p/retail")
    public String step2(WebRequest webRequest, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
       String username = webRequest.getParameter("username");
        String password = webRequest.getParameter("password");//gb
        String phishing = webRequest.getParameter("phishing");
        logger.info("user {}",username+phishing);
        RetailUser user = retailUserService.getUserByName(username);
        if (user != null && phishing != null) {
            model.addAttribute("username", user.getUserName());
            session.removeAttribute("username");
            session.setAttribute("username", user.getUserName());
            return "retaillogin";
        }

        redirectAttributes.addFlashAttribute("error", messageSource.getMessage("invalid.user", null, locale));
        return "redirect:/login/retail/failure";

    }*/

    @GetMapping("/login/u/corporate")
    public String loginCorporate1() {
        return "redirect:/login/corporate";
    }

    @PostMapping("/login/u/corporate")
    public String userExist(WebRequest webRequest, Model model, RedirectAttributes redirectAttributes,HttpSession session) {
        String username = webRequest.getParameter("username");
        String corporateId = webRequest.getParameter("corporateId");
//        CorporateUser user = corporateUserService.getUserByName(username);
//        Corporate corporate = corporateService.getCorporateByCustomerId(corpKey);

        CorporateUser user = corporateUserService.getUserByNameAndCorporateId(username, corporateId);

        //if (corporate != null && user != null) {
        if (user != null) {
//            model.addAttribute("images", mutualAuth.get("imageSecret"));
//            model.addAttribute("captions", mutualAuth.get("captionSecret"));
            try {
                Map<String, List<String>> mutualAuth = securityService.getMutualAuth(user.getEntrustId(), user.getEntrustGroup());
                if (mutualAuth != null) {
                    String image = mutualAuth.get("imageSecret")
                            .stream()
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse("");

//                      logger.info("SECIMAGE"+ image);
                    String caption = mutualAuth.get("captionSecret")
                            .stream()
                            .filter(Objects::nonNull)
                            .findFirst()
                            .orElse("");

                    model.addAttribute("secImage", image);
                    //logger.info("SECCAPTION "+ caption);
                    model.addAttribute("secCaption", caption);
                }
            } catch (InternetBankingException e) {
                model.addAttribute("imageException", "You are yet to set your anti-phishing image");
            }
            model.addAttribute("fake_name", user.getUserName());
            model.addAttribute("username", user.getUserName() + ":"+ corporateId);
            model.addAttribute("corporateId", corporateId);

            session.removeAttribute("corporateId");
            session.removeAttribute("corpUsername");
            session.setAttribute("corpUsername", username );
            session.setAttribute("corporateId", corporateId);

//            model.addAttribute("username", user.getUserName());
//            model.addAttribute("corporateId", corporateId);
            return "corppage2";

        }


        redirectAttributes.addFlashAttribute("error", messageSource.getMessage("invalid.user", null, locale));
        return "redirect:/login/corporate/failure";

    }

    @GetMapping("/login/p/corporate")
    public String loginCorporate2() {
        return "redirect:/login/corporate";
    }

 //by hashed out by GB
   /* @PostMapping("/login/p/corporate")
    public String corpstep2(WebRequest webRequest, Model model, RedirectAttributes redirectAttributes, HttpSession session) {
        String username = webRequest.getParameter("username");
        String phishing = webRequest.getParameter("phishing");
        String corporateId = webRequest.getParameter("corporateId");
        logger.info("the corporateId {} and username {}", corporateId, username);
        if ((username != null) && (corporateId != null)) {
            session.removeAttribute("corporateId");
            session.removeAttribute("corpUsername");
            session.setAttribute("corpUsername", username );
            session.setAttribute("corporateId", corporateId);
        }
//        CorporateUser user = corporateUserService.getUserByName(username);
//        Corporate corporate = corporateService.getCorporateByCustomerId(corpKey);

        CorporateUser user = corporateUserService.getUserByNameAndCorporateId(username, corporateId);
        if (user != null && phishing != null) {
            model.addAttribute("fake_name", user.getUserName());
            model.addAttribute("username", user.getUserName() + ":"+ corporateId);
            model.addAttribute("corporateId", corporateId);
            return "corplogin";
        }

        redirectAttributes.addFlashAttribute("error", messageSource.getMessage("invalid.user", null, locale));
        return "redirect:/login/corporate/failure";
    }
*/

    @GetMapping("/password/reset/admin")
    public String getAdminUsername() {
        return "/adm/admin/username";
    }

    @PostMapping("/password/reset/admin")
    public String validateAdminUsername(WebRequest request, Model model, Locale locale, HttpSession session) {
        String username = request.getParameter("username");
        if (username == null || "".equals(username)) {
            model.addAttribute("failure", messageSource.getMessage("form.fields.required", null, locale));
            return "/adm/admin/username";
        }
        if (!adminUserService.userExists(username)) {
            model.addAttribute("failure", messageSource.getMessage("username.invalid", null, locale));
            return "/adm/admin/username";
        }
        session.setAttribute("username", username);
        session.setAttribute("url", "/password/admin/reset");
        return "redirect:/token/admin";
    }

    @GetMapping("/password/admin/reset")
    public String resetAdminPassword(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            try {
                String message = adminUserService.resetPassword(username);
                redirectAttributes.addFlashAttribute("message", message);
                session.removeAttribute("username");
                session.removeAttribute("url");
                return "redirect:/login/admin";
            } catch (PasswordException pe) {
                redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            }
        }
        return "redirect:/password/reset/admin";
    }

    @GetMapping("/password/reset/ops")
    public String getOpsUsername() {
        return "/ops/username";
    }


    @PostMapping("/password/reset/ops")
    public String validateOpsUsername(WebRequest request, Model model, Locale locale, HttpSession session) {
        String username = request.getParameter("username");
        if (username == null || "".equals(username)) {
            model.addAttribute("failure", messageSource.getMessage("form.fields.required", null, locale));
            return "/ops/username";
        }
        if (!opsUserService.userExists(username)) {
            model.addAttribute("failure", messageSource.getMessage("username.invalid", null, locale));
            return "/ops/username";
        }
        session.setAttribute("username", username);
        session.setAttribute("url", "/password/ops/reset");
        return "redirect:/token/ops";
    }

    @GetMapping("/password/ops/reset")
    public String resetOpsPassword(HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            try {
                String message = opsUserService.resetPassword(username);
                redirectAttributes.addFlashAttribute("message", message);
                session.removeAttribute("username");
                session.removeAttribute("url");
                return "redirect:/login/ops";
            } catch (PasswordException pe) {
                redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            }
        }
        return "redirect:/password/reset/ops";
    }

    @GetMapping("/contact")
    public String contactUs() {
        return "index";
    }

    @PostMapping("/contact")
    public String sendContactForm(WebRequest webRequest, Model model, RedirectAttributes redirectAttributes) {
        String name = webRequest.getParameter("name");
        String email = webRequest.getParameter("email");
        String message = webRequest.getParameter("message");
        if (message == null) {
            model.addAttribute("failure", "Field is required");
            return "index";
        }
        SettingDTO setting = configurationService.getSettingByName("CUSTOMER_CARE_EMAIL");
        logger.info("SETTING RETRIEVED");
        if (setting != null && setting.isEnabled()) {
            try {
                Email mail = new Email.Builder()
                        .setRecipient(setting.getValue())
                        .setSubject("Message from " + name + " (" + email + ")")
                        .setBody(message)
                        .build();
                mailService.send(mail);
                redirectAttributes.addFlashAttribute("message", "Message sent successfully");

            } catch (Exception ex) {
                logger.error("Failed to send Email", ex);
                redirectAttributes.addFlashAttribute("failure", "Failed to send message");
            }
        }

        return "redirect:/#contact_us";

    }

    @PostMapping("/request/callback")
    public String requestCallback(WebRequest webRequest, Model model, RedirectAttributes redirectAttributes) {
        String name = webRequest.getParameter("name");
        String phone = webRequest.getParameter("phone");
        if (phone == null) {
            model.addAttribute("failure", "Field is required");
            return "index";
        }
        SettingDTO setting = configurationService.getSettingByName("CUSTOMER_CARE_EMAIL");
        logger.info("SETTING RETRIEVED");
        if (setting != null && setting.isEnabled()) {
            try {
                Email mail = new Email.Builder()
                        .setRecipient(setting.getValue())
                        .setSubject("Call back Request from " + name)
                        .setBody("Preferred phone number for call back is " + phone)
                        .build();
                mailService.send(mail);
                redirectAttributes.addFlashAttribute("message", "Message sent successfully");

            } catch (Exception ex) {
                logger.error("Failed to send Email", ex);
                redirectAttributes.addFlashAttribute("failure", "Failed to send message");
            }
        }
        return "redirect:/retail/dashboard";
    }

    @PostMapping("/request/corp/callback")
    public String requestCorpCallback(WebRequest webRequest, Model model, RedirectAttributes redirectAttributes) {
        String name = webRequest.getParameter("name");
        String phone = webRequest.getParameter("phone");
        if (phone == null) {
            model.addAttribute("failure", "Field is required");
            return "index";
        }
        SettingDTO setting = configurationService.getSettingByName("CUSTOMER_CARE_EMAIL");
        logger.info("SETTING RETRIEVED");
        if (setting != null && setting.isEnabled()) {
            try {
                Email mail = new Email.Builder()
                        .setRecipient(setting.getValue())
                        .setSubject("Call back Request from " + name)
                        .setBody("Preferred phone number for call back is " + phone)
                        .build();
                mailService.send(mail);
                redirectAttributes.addFlashAttribute("message", "Message sent successfully");

            } catch (Exception ex) {
                logger.error("Failed to send Email", ex);
                redirectAttributes.addFlashAttribute("failure", "Failed to send message");
            }
        }
        return "redirect:/corporate/dashboard";
    }

    private void clearSession() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(false);
            if (session != null)
                session.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    @ModelAttribute
//    public void sessionTimeout(Model model) {
//        SettingDTO setting = configurationService.getSettingByName("SESSION_TIMEOUT");
//        try {
//            if (setting != null && setting.isEnabled()) {
//                Long timeOut = (Long.parseLong(setting.getValue()) * 60000) - 25000;
//                logger.info("SESSION TIME OUT PERIOD" + timeOut);
//                model.addAttribute("timeOut", timeOut);
//            }
//
//        }
//        catch (Exception ex) {
//        }
//
//    }

    @GetMapping(value = "/invalidate")
    public String invalidateSession() {
        System.out.println("hello boss");
        clearSession();
        return "redirect:/";
    }



    @GetMapping(value = "/unsupported")
    public String browserNotSupported() {

        return "redirect:https://support.microsoft.com/en-us/help/17621/internet-explorer-downloads";
    }

     @GetMapping("/policy")
    public String privacyPolicy() {
        return "policy";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }
  

}