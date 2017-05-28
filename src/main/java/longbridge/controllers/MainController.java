package longbridge.controllers;


import com.sun.javafx.sg.prism.NGShape;
import longbridge.exception.PasswordException;
import longbridge.exception.UnknownResourceException;
import longbridge.models.RetailUser;
import longbridge.services.AdminUserService;
import longbridge.services.OperationsUserService;
import longbridge.services.RetailUserService;
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

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by Wunmi on 27/03/2017.
 */
@Controller
public class MainController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RetailUserService retailUserService;
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private OperationsUserService opsUserService;
    @Autowired
    private MessageSource messageSource;



    @RequestMapping(value = {"/", "/home"})
    public String getHomePage() {
        return "index";
    }

    @RequestMapping(value = "/login/retail", method = RequestMethod.GET)
    public ModelAndView getLoginPage(@RequestParam Optional<String> error) {
        return new ModelAndView("retaillogin", "error", error);
    }

    @RequestMapping(value = "/login/corporate", method = RequestMethod.GET)
    public ModelAndView getCorpLoginPage(@RequestParam Optional<String> error) {
        return new ModelAndView("corplogin", "error", error);
    }

    @GetMapping(value = "/login/admin")
    public ModelAndView adminLogin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admlogin");
        return modelAndView;
    }

    @GetMapping(value = "/login/ops")
    public ModelAndView opsLogin() {
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




    @GetMapping(value = {"/retail/{path:(?!static).*$}","/retail/{path:(?!static).*$}/**" })
    public String retailUnknown(Principal principal){
        if (principal!=null){
            return "redirect:/retail/dashboard";

        }

       throw new UnknownResourceException();
     //   return "";
    }


    @GetMapping(value = {"/admin/{path:(?!static).*$}","/admin/{path:(?!static).*$}/**" })
    public String adminUnknown(Principal principal){
        if (principal!=null){

            return "redirect:/admin/dashboard";

        }

     throw new UnknownResourceException();
       // return "";
    }

    @GetMapping(value = {"/ops/{path:(?!static).*$}","/ops/{path:(?!static).*$}/**" })
    public String opsUnknown(Principal principal){
        if (principal!=null){

            return "redirect:/ops/dashboard";

        }

        throw new UnknownResourceException();
       // return "";
    }

    @PostMapping("/user/exists")
    public @ResponseBody boolean userExists(WebRequest webRequest){
        String username = webRequest.getParameter("username");
        RetailUser user =  retailUserService.getUserByName(username);
        if (user == null){
            return false;
        }
        return true;
    }

    @GetMapping("/password/reset/admin")
    public String getAdminUsername(){
        return "/adm/admin/username";
    }

    @PostMapping("/password/reset/admin")
    public String validateAdminUsername(WebRequest request, Model model, Locale locale, HttpSession session){
        String username = request.getParameter("username");
        if(username==null||"".equals(username)){
            model.addAttribute("failure",messageSource.getMessage("form.fields.required",null,locale));
            return "/adm/admin/username";
        }
        if(!adminUserService.userExists(username)){
            model.addAttribute("failure",messageSource.getMessage("username.invalid",null,locale));
            return "/adm/admin/username";
        }
        session.setAttribute("username",username);
        session.setAttribute("url","/password/admin/reset");
        return "redirect:/token/admin";
    }

    @GetMapping("/password/admin/reset")
    public String resetAdminPassword(HttpSession session, RedirectAttributes redirectAttributes){
        if(session.getAttribute("username")!=null){
            String username =(String)session.getAttribute("username");
            try {
                String message = adminUserService.resetPassword(username);
                redirectAttributes.addFlashAttribute("message",message);
                session.removeAttribute("username");
                session.removeAttribute("url");
                return "redirect:/login/admin";
            }
            catch (PasswordException pe){
                redirectAttributes.addFlashAttribute("failure",pe.getMessage());
            }
        }
        return "redirect:/password/reset/admin";
    }

    @GetMapping("/password/reset/ops")
    public String getOpsUsername(){
        return "/ops/username";
    }

    @PostMapping("/password/reset/ops")
    public String validateOpsUsername(){
        return "/ops/token";
    }

    @PostMapping("/password/reset/ops")
    public String validateOpsUsername(WebRequest request, Model model, Locale locale, HttpSession session){
        String username = request.getParameter("username");
        if(username==null||"".equals(username)){
            model.addAttribute("failure",messageSource.getMessage("form.fields.required",null,locale));
            return "/ops/username";
        }
        if(!opsUserService.userExists(username)){
            model.addAttribute("failure",messageSource.getMessage("username.invalid",null,locale));
            return "/ops/username";
        }
        session.setAttribute("username",username);
        session.setAttribute("url","/password/ops/reset");
        return "redirect:/token/ops";
    }

    @GetMapping("/password/ops/reset")
    public String resetOpsPassword(HttpSession session, RedirectAttributes redirectAttributes){
        if(session.getAttribute("username")!=null){
            String username =(String)session.getAttribute("username");
            try {
                String message = opsUserService.resetPassword(username);
                redirectAttributes.addFlashAttribute("message",message);
                session.removeAttribute("username");
                session.removeAttribute("url");
                return "redirect:/login/admin";
            }
            catch (PasswordException pe){
                redirectAttributes.addFlashAttribute("failure",pe.getMessage());
            }
        }
        return "redirect:/password/reset/ops";
    }

}