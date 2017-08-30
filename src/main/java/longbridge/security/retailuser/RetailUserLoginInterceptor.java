package longbridge.security.retailuser;

import longbridge.forms.ChangeDefaultPassword;
import longbridge.forms.CustChangePassword;
import longbridge.forms.CustResetPassword;
import longbridge.services.PasswordPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
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
        String uri = httpServletRequest.getRequestURI();





        if (httpServletRequest.getSession().getAttribute("expired-password") != null && !(uri.equalsIgnoreCase("/retail/reset_password"))) {
            CustResetPassword resetPassword = new CustResetPassword();

            ModelAndView modelAndView = new ModelAndView("forwarded-view");

            modelAndView.addObject("custResetPassword", resetPassword);
            modelAndView.addObject("passwordRules", passwordPolicyService.getPasswordRules());

            modelAndView.setViewName("cust/settings/new-pword");
            throw new ModelAndViewDefiningException(modelAndView);

        }

        if (httpServletRequest.getSession().getAttribute("2FA")!=null&& !(uri.equalsIgnoreCase("/retail/token")))
        {

            ModelAndView modelAndView = new ModelAndView("forwarded-view");

            modelAndView.setViewName("/cust/logintoken");
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
