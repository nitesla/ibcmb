//package longbridge.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.web.ErrorAttributes;
//import org.springframework.boot.autoconfigure.web.ErrorController;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import java.security.Principal;
//import java.util.Map;
//
///**
// * Created by Fortune on 8/6/2017.
// */
//
//@Controller
//public class ApplicationErrorController implements ErrorController {
//
//    private static final String PATH = "/error";
//
//
//    @Autowired
//    private ErrorAttributes errorAttributes;
//
//    @RequestMapping(value = PATH)
//    @ResponseBody
//    public Map handleError(HttpServletRequest request, Model model, Principal principal){
//
//        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
//        Map<String,Object> errorDetails = errorAttributes.getErrorAttributes(requestAttributes,true);
//        model.addAttribute("errors",errorDetails);
//
//        return errorDetails;
//    }
//
//
//
//    @Override
//    public String getErrorPath() {
//        return PATH;
//    }
//}
