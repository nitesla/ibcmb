package longbridge.security.opsuser;

import longbridge.forms.ChangeDefaultPassword;
import longbridge.models.User;
import longbridge.models.UserType;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.services.PasswordPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class OpUserLoginInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private PasswordPolicyService passwordPolicyService;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        String uri=httpServletRequest.getRequestURI();

        if (getCurrentUser()==null) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() +"/login/ops");
            return false;
        }

        if (getCurrentUser()!=null && !UserType.OPERATIONS.equals(getCurrentUser().getUserType())) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() +"/login/ops");
            return false;
        }


        if (httpServletRequest.getSession().getAttribute("expired-password")!=null&& !(uri.equalsIgnoreCase("/ops/users/password/new")))
        {
            ChangeDefaultPassword changePassword = new ChangeDefaultPassword();
            ModelAndView modelAndView = new ModelAndView("forwarded-view");

            modelAndView.addObject("changePassword", changePassword);
          modelAndView.addObject("passwordRules", passwordPolicyService.getPasswordRules());

            modelAndView.setViewName("ops/new-pword");
            throw new ModelAndViewDefiningException(modelAndView);
        }

        if (httpServletRequest.getSession().getAttribute("2FA")!=null&& !(uri.equalsIgnoreCase("/ops/token")))
        {

            ModelAndView modelAndView = new ModelAndView("forwarded-view");

            modelAndView.setViewName("/ops/token");
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
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            CustomUserPrincipal currentUser = (CustomUserPrincipal) authentication.getPrincipal();
            return currentUser.getUser();
        }

        return null;
    }




}
