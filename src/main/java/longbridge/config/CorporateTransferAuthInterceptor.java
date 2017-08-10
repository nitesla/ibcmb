package longbridge.config;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.services.ConfigurationService;
import longbridge.utils.TransferType;
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
public class CorporateTransferAuthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ConfigurationService configService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");


        if (
/* service to check if token is enabled comes in here  */
                (setting != null && setting.isEnabled())
                        &&
                        httpServletRequest.getSession().getAttribute("auth-needed") == null
                        &&  httpServletRequest.getSession().getAttribute("corpTransferRequest")!=null
                        && !((CorpTransferRequestDTO) httpServletRequest.getSession().getAttribute("corpTransferRequest")).getTransferType().equals(TransferType.OWN_ACCOUNT_TRANSFER)


                ) {
            httpServletRequest.getSession().setAttribute("auth-needed", "auth-needed");
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/corporate/transfer/auth");
            return false;

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
