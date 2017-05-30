package longbridge.security.corpuser;

import longbridge.forms.ChangeDefaultPassword;
import longbridge.services.PasswordPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ayoade_farooq@yahoo.com on 5/15/2017.
 */
@Component
public class CorporateUserLoginInterceptor extends HandlerInterceptorAdapter {
@Autowired
private PasswordPolicyService passwordPolicyService;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String uri=httpServletRequest.getRequestURI();



        if (httpServletRequest.getSession().getAttribute("expired-password")!=null&& !(uri.equalsIgnoreCase("/corporate/reset_password")))
        {
            ChangeDefaultPassword changePassword = new ChangeDefaultPassword();

            ModelAndView modelAndView = new ModelAndView("forwarded-view");
            modelAndView.addObject("changePassword", changePassword);
            modelAndView.addObject("passwordRules", passwordPolicyService.getPasswordRules());

            modelAndView.setViewName("corp/settings/new-password");
            throw new ModelAndViewDefiningException(modelAndView);
        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {










    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}