package longbridge.controllers.admin;

import longbridge.dtos.CustomerFeedBackSummaryDTO;
import longbridge.services.CustomerFeedBackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("admin/feedback")
public class AdmFeedBackSummaryController {

    @Autowired
    private CustomerFeedBackService customerFeedBackService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/enquire")
    public String feedBackEnquiry() {
        return "adm/feedback/summary";
    }


    @PostMapping("/enquire")
    public String feedBackEnquiry(WebRequest webRequest, Model model) throws Throwable {
        String searchTitle="";
        Date startDate=null, endDate=null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonth().getValue();
        int day = now.getDayOfMonth();
        String period = webRequest.getParameter("period");
        if ("cMonth".equals(period)) {
            searchTitle="Feedback Summary For Current Month";
            String startday = "01";
            String start = year + "-" + month + "-" + startday;
            String end = year + "-" + month + "-" + day;

            startDate = format.parse(start);
            Date end1 = format.parse(end);
            endDate = new Date(end1.getTime() + TimeUnit.DAYS.toMillis(1));
        }
        if ("oneMonth".equals(period)) {
            searchTitle="Feedback Summary For Last One Month";
            int lastMonth = 0;
            int currentYear = 0;
            if (month > 1) {
                lastMonth = month - 1;
                currentYear = year;
            }
            if (month == 1) {
                lastMonth = 12;
                currentYear = year - 1;
            }
            String start = currentYear + "-" + lastMonth + "-" + day;
            String end = year + "-" + month + "-" + day;
            startDate = format.parse(start);
            Date end1 = format.parse(end);
            endDate = new Date(end1.getTime() + TimeUnit.DAYS.toMillis(1));
        }
        if ("threeMonths".equals(period)) {
            searchTitle="Feedback Summary For Last Three Months";
            int lastThreeMonths = 0;
            int currentYear = 0;
            if (month > 3) {
                lastThreeMonths = month - 3;
                currentYear = year;
            }
            if (month == 3 || month < 3) {
                lastThreeMonths = month + 9;//12
                currentYear = year - 1;
            }
            String start = currentYear + "-" + lastThreeMonths + "-" + day;
            String end = year + "-" + month + "-" + day;
            startDate = format.parse(start);
            Date end1 = format.parse(end);
            endDate = new Date(end1.getTime() + TimeUnit.DAYS.toMillis(1));
        }
        if ("fix".equals(period)) {
            String start = webRequest.getParameter("start");
            String end = webRequest.getParameter("end");
            searchTitle="Feedback Summary from "+""+start+""+" to "+""+end;
            startDate = format.parse(start);
            Date end1 = format.parse(end);
            endDate = new Date(end1.getTime() + TimeUnit.DAYS.toMillis(1));
        }

        List<CustomerFeedBackSummaryDTO> summary = customerFeedBackService.getCustomerFeedBackSummary(startDate, endDate);
        model.addAttribute("searchTitle",searchTitle);
        model.addAttribute("summary", summary);
        logger.info("feedback is {}", summary);
        logger.info("year,month{},{}", startDate, endDate);

        return "adm/feedback/summary";

    }
}



