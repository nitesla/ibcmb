package longbridge.controllers;

import longbridge.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * Created by Fortune on 5/26/2017.
 */
@Controller
@RequestMapping("/token")
public class TokenAuthController {

    @Autowired
    private SecurityService securityService;

    @PostMapping("/authenticate")
    public String authenticate(HttpSession session){

        //TODO authenticate
        String url = "";
        if(session.getAttribute("url")!=null) {
            url = (String) session.getAttribute("url");
        }

        return "redirect:"+url;
    }

    @GetMapping("/admin")
    public String getAdminToken(){
        return "adm/admin/token";
    }


    @GetMapping("/ops")
    public String getOpsToken(){
        return "ops/token";
    }
}
