package longbridge.controllers;

import longbridge.models.Email;
import longbridge.services.MailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Fortune on 8/6/2017.
 */

@Controller
public class ApplicationErrorController implements ErrorController {

    private static final String PATH = "/error";

    @Autowired
    private MailService mailService;

    @Autowired
    private ErrorAttributes errorAttributes;


    @RequestMapping(value = PATH)
    public String handleError(HttpServletRequest request) {

        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        Map<String, Object> errorDetails = errorAttributes.getErrorAttributes(requestAttributes, true);




        String errorPath = (String) errorDetails.get("path");
        String statusCode = errorDetails.get("status").toString();
        Object exception = errorDetails.get("exception");


        if (exception != null) {
            sendNotification(errorDetails);
        }

        if("403".equals(statusCode)){
            return "/error403";
        }

        String subPath = StringUtils.substringAfter(errorPath, "/");

        if (subPath != null) {
            if (subPath.startsWith("admin")) {
                return "redirect:/admin/error";
            } else if (subPath.startsWith("ops")) {
                return "redirect:/ops/error";
            } else if (subPath.startsWith("retail")) {
                return "redirect:/retail/error";
            } else if (subPath.startsWith("corporate")) {
                return "redirect:/corporate/error";
            }
        }

        return "/error500";
    }


    @Override
    public String getErrorPath() {
        return PATH;
    }


    private void sendNotification(Map errorDetails) {
        String time = errorDetails.get("timestamp").toString();
        String statusCode = errorDetails.get("status").toString();
        String error = errorDetails.get("error").toString();
        String exception = (String) errorDetails.get("exception");
        String message = errorDetails.get("message").toString();
        String trace = (String) errorDetails.get("trace");
        String path = (String) errorDetails.get("path");

        if (!"405".equals(statusCode)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Time: " + time + "\n")
                    .append("Path: " + path + "\n")
                    .append("Status Code: " + statusCode + "\n")
                    .append("Error: " + error + "\n")
                    .append("Exception: " + exception + "\n")
                    .append("Message: " + message + "\n")
                    .append("Trace: " + trace + "\n");

            String[] ccList = {"farooq.ayoade@longbridgetech.com", "oluwawunmi.sowunmi@longbridgetech.com"};
            Email email = new Email.Builder().setRecipient("fortunatus.ekenachi@longbridgetech.com")
                    .setCCList(ccList)
                    .setSubject(error)
                    .setBody(builder.toString())
                    .build();
            new Thread(() -> mailService.send(email)).start();
        }


    }
}
