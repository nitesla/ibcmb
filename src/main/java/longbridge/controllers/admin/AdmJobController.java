package longbridge.controllers.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by Longbridge on 6/28/2017.
 */
@Controller
@RequestMapping("admin/job")
public class AdmJobController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @GetMapping
    public String manage() {
        return "adm/job/manage-job";
    }

    @PostMapping("/expression")
    public String getCronExpression(WebRequest webRequest) {
        String cronExpr = "";
        String second = webRequest.getParameter("second");
        String minute = webRequest.getParameter("minute");
        String[] hour = webRequest.getParameterValues("hour");
        String[] week = webRequest.getParameterValues("week");
        String[] month = webRequest.getParameterValues("month");
        logger.info("second {} minute{} hour {} week {} month{}",second,minute,hour,week,month);
        return "adm/job/manage-job";
    }
}