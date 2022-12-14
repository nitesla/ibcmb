package longbridge.config;

import longbridge.dtos.SettingDTO;
import longbridge.dtos.TransferRequestDTO;
import longbridge.services.SettingsService;
import longbridge.utils.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ayoade_farooq@yahoo.com on 5/15/2017.
 */
@Component
public class RetailTransferAuthInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SettingsService configService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");


        if (
/* service to check if token is enabled comes in here  */
                (setting != null && setting.isEnabled())
                        &&
                        httpServletRequest.getSession().getAttribute("auth-needed") == null
                &&  httpServletRequest.getSession().getAttribute("transferRequest")!=null
                && !((TransferRequestDTO) httpServletRequest.getSession().getAttribute("transferRequest")).getTransferType().equals(TransferType.OWN_ACCOUNT_TRANSFER)
                ) {

//            if (httpServletRequest.getParameter("add") != null)
//                httpServletRequest.getSession().setAttribute("add", "add");


            httpServletRequest.getSession().setAttribute("auth-needed", "auth-needed");
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/retail/transfer/auth");
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
