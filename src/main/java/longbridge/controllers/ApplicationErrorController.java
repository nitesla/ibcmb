package longbridge.controllers;

import longbridge.models.User;
import longbridge.security.userdetails.CustomUserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ShellProperties;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

/**
 * Created by Fortune on 8/6/2017.
 */

@Controller
public class ApplicationErrorController implements ErrorController {

    private static final String PATH = "/error";


    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    public String handleError(HttpServletRequest request, Model model, Principal principal) {

        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        Map<String, Object> errorDetails = errorAttributes.getErrorAttributes(requestAttributes, true);
        model.addAttribute("errors", errorDetails);

        String errorPath = (String) errorDetails.get("path");

        String subPath = StringUtils.substringAfter(errorPath, "/");


        if (subPath.startsWith("admin")) {
            return "redirect:/admin/error";
        }
        else if (subPath.startsWith("ops")) {
            return "redirect:/ops/error";
        }
        else if (subPath.startsWith("retail")) {
            return "redirect:/retail/error";
        }
        else if (subPath.startsWith("corporate")) {
            return "redirect:/corporate/error";
        }


        return "/error";
    }




    @GetMapping("/admin/error")
    public String getAdminErrorPage() {
        return "/adm/error";

    }

    @GetMapping("/ops/error")
    public String getOpsErrorPage() {
        return "/ops/error";

    }
    @GetMapping("/corporate/error")
    public String getCorporateErrorPage() {
        return "/corp/error";

    }
    @GetMapping("/retail/error")
    public String getRetailErrorPage() {
        return "/cust/error";

    }


    @Override
    public String getErrorPath() {
        return PATH;
    }
}
