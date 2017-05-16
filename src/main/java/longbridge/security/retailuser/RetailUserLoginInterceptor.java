package longbridge.security.retailuser;

import longbridge.forms.ChangeDefaultPassword;
import longbridge.services.PasswordPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ayoade_farooq@yahoo.com on 5/15/2017.
 */
@Component
public class RetailUserLoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private PasswordPolicyService passwordPolicyService;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String uri=httpServletRequest.getRequestURI();
        if (uri.equalsIgnoreCase("/admin/users/password/new"))
            return false;
        //janan
        System.out.println("E DEY ENTER INTERCEPTOR");

        if (httpServletRequest.getSession().getAttribute("expired-password")!=null){

            return true;

        }

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {



        System.out.println("The guy is  expired jare");
        ChangeDefaultPassword changePassword = new ChangeDefaultPassword();
        modelAndView.setViewName("/adm/admin/new-pword");

        modelAndView.addObject("changePassword", changePassword);
        modelAndView.addObject("passwordRules", passwordPolicyService.getPasswordRules());
        System.out.println("i hope it works");




    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }




}
