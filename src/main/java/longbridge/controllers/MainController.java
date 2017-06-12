package longbridge.controllers;


import longbridge.exception.PasswordException;
import longbridge.exception.UnknownResourceException;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.RetailUser;
import longbridge.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.*;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Controller
public class MainController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale;

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
    private CorporateService corporateService;
    @Autowired
    private SecurityService securityService;


    @RequestMapping(value = {"/", "/home"})
    public String getHomePage(@RequestParam Optional<HttpServletRequest> request) {

        if (request.isPresent()) request.get().getSession().invalidate();

        return "index";
    }

    @RequestMapping(value = "/login/retail", method = RequestMethod.GET)
    public ModelAndView getLoginPage(@RequestParam Optional<String> error, @RequestParam Optional<HttpServletRequest> request) {

        if (request.isPresent()) request.get().getSession().invalidate();


        return new ModelAndView("retpage1", "error", error);
    }

    @RequestMapping(value = "/login/corporate", method = RequestMethod.GET)
    public ModelAndView getCorpLoginPage(@RequestParam Optional<String> error) {

        return new ModelAndView("corppage1", "error", error);
    }

    @GetMapping(value = "/login/admin")
    public ModelAndView adminLogin(@RequestParam Optional<HttpSession> session) {
        if (session.isPresent()) session.get().invalidate();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admlogin");
        return modelAndView;
    }

    @GetMapping(value = "/login/ops")
    public ModelAndView opsLogin(@RequestParam Optional<HttpSession> session) {

        if (session.isPresent()) session.get().invalidate();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("opslogin");
        return modelAndView;
    }

    @RequestMapping(value = {"/admin/dashboard", "/admin"})
    public String getAdminDashboard() {
        return "adm/dashboard";
    }


    @GetMapping("/faqs")
    public String viewFAQs() {
        return "cust/faqs"; //TODO
    }

    @GetMapping("/login/retail/failure")
    public String retLoginFailure() {


        return "retloginfailure";
    }

    @GetMapping("/login/corporate/failure")
    public String corpLoginFailure() {

        return "corploginfailure";
    }


    @GetMapping(value = {"/retail/{path:(?!static).*$}","/retail/{path:(?!static).*$}/**" })
    public String retailUnknown(Principal principal){
        if (principal!=null){

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

    @PostMapping("/login/u/retail")
    public String userExists(WebRequest webRequest, Model model, RedirectAttributes redirectAttributes) {
        String username = webRequest.getParameter("username");

        RetailUser user =  retailUserService.getUserByName(username);
        if (user == null){
            return "redirect:/login/retail/failure";
        }

//        Map<String, List<String>> mutualAuth =  securityService.getMutualAuth(username);
//        String image = mutualAuth.get("imageSecret")
//        .stream()
//        .filter(Objects::nonNull)
//        .findFirst()
//        .orElse("");
//
//        logger.info("SECIMAGE{}", image);
//
//        model.addAttribute("secImage", image);
        model.addAttribute("username", user.getUserName());
        return "retpage2";
    }

    @PostMapping("/login/p/retail")
    public String step2(WebRequest webRequest, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String username = webRequest.getParameter("username");
        String phishing = webRequest.getParameter("phishing");

        RetailUser user =  retailUserService.getUserByName(username);
        if (user != null && phishing != null) {
            model.addAttribute("username", user.getUserName());
            session.setAttribute("username", user.getUserName());
            return "retaillogin";
        }

        redirectAttributes.addFlashAttribute("error", messageSource.getMessage("invalid.user", null, locale));
        return "redirect:/login/retail/failure";

    }


    @PostMapping("/login/u/corporate")
    public String userExist(WebRequest webRequest, Model model, RedirectAttributes redirectAttributes) {
        String username = webRequest.getParameter("username");
        String corpKey = webRequest.getParameter("corporateId");
        CorporateUser user = corporateUserService.getUserByName(username);
        Corporate corporate = corporateService.getCorporateByCustomerId(corpKey);
//        Map<List<String>, List<String>> mutualAuth = securityService.getMutualAuth(user.getUserName());

        //get map

        if (corporate != null && user != null) {
//            model.addAttribute("images", mutualAuth.get("imageSecret"));
//            model.addAttribute("captions", mutualAuth.get("captionSecret"));
            model.addAttribute("username", user.getUserName());
            model.addAttribute("corpKey", corpKey);
            return "corppage2";
        }


        redirectAttributes.addFlashAttribute("error", messageSource.getMessage("invalid.user", null, locale));
        return "redirect:/login/corporate/failure";

    }

    @PostMapping("/login/p/corporate")
    public String corpstep2(WebRequest webRequest, Model model, RedirectAttributes redirectAttributes) {
        String username = webRequest.getParameter("username");
        String phishing = webRequest.getParameter("phishing");
        String corpKey = webRequest.getParameter("corpKey");
        CorporateUser user = corporateUserService.getUserByName(username);
        Corporate corporate = corporateService.getCorporateByCustomerId(corpKey);
        if (corporate != null && user != null && phishing != null) {
            model.addAttribute("username", user.getUserName());
            model.addAttribute("corpKey", corpKey);
            return "corplogin";
        }

        redirectAttributes.addFlashAttribute("error", messageSource.getMessage("invalid.user", null, locale));
        return "redirect:/login/corporate/failure";
    }


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
                return "redirect:/login/admin";
            } catch (PasswordException pe) {
                redirectAttributes.addFlashAttribute("failure", pe.getMessage());
            }
        }
        return "redirect:/password/reset/ops";
    }

}