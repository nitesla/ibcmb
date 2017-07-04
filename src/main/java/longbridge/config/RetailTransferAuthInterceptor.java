package longbridge.config;

import longbridge.dtos.SettingDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.forms.ChangeDefaultPassword;
import longbridge.services.ConfigurationService;
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
public class RetailTransferAuthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ConfigurationService configService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");


        if (/* service to check if token is enabled comes in here  */

                (setting != null && setting.isEnabled())
                        &&
                        httpServletRequest.getSession().getAttribute("auth-needed") == null

                ) {


            httpServletRequest.getSession().setAttribute("auth-needed", "auth-needed");

            ModelAndView view = new ModelAndView("forwarded-view");
            TransferRequestDTO dto = (TransferRequestDTO) httpServletRequest.getSession().getAttribute("transferRequest");

            if (dto != null) view.addObject("transferRequest", dto);

            view.setViewName("/cust/transfer/transferauth");

//                 httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/retail/transfer/interbank/auth");

            throw new ModelAndViewDefiningException(view);

        }


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

        // String uri=httpServletRequest.getRequestURI();


        // if (httpServletRequest.getSession().getAttribute("auth-needed")!=null)

//          if ( httpServletRequest.getSession().getAttribute("AUTH") !=null){
//
//
//
//              TransferRequestDTO dto= (TransferRequestDTO) httpServletRequest.getSession().getAttribute("transferRequest");
//
//              if (dto!=null) modelAndView.addObject("transferRequest", dto);
//
//
//              //modelAndView.addObject("passwordRules", passwordPolicyService.getPasswordRules());
//
//              modelAndView.setViewName("/transfer/transferauth");
//          }


    }


    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
