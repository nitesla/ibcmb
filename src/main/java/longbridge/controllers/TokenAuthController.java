package longbridge.controllers;

import longbridge.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

/**
 * Created by Fortune on 5/26/2017.
 */
public class TokenAuthController {

    @Autowired
    private SecurityService securityService;

    @GetMapping("/authenticate")
    public String authenticate(HttpSession session){

        return "";
    }
}
