package longbridge.security.adminuser;

import longbridge.forms.ChangePassword;
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
public class AdminUserLoginInterceptor extends HandlerInterceptorAdapter {
@Autowired
private PasswordPolicyService passwordPolicyService;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String uri=httpServletRequest.getRequestURI();
        if (uri.equalsIgnoreCase("/admin/users/password/new"))
 return false;
        System.out.println("E DEY ENTER INTERCEPTOR");

        if (httpServletRequest.getSession().getAttribute("expired-password")!=null){
             System.out.println("The guy is  expired jare");
             return true;

         }

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {



            System.out.println("The guy is  expired jare");
            ChangeDefaultPassword changePassword = new ChangePassword();
            modelAndView.setViewName("adm/admin/new-password");

            modelAndView.addObject("changePassword", changePassword);
           // modelAndView.addObject("passwordRules", passwordPolicyService.getPasswordRules());
            System.out.println("i hope it works");




    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
