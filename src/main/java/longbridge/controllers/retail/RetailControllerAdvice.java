package longbridge.controllers.retail;

import longbridge.models.RetailUser;
import longbridge.models.ServiceReqConfig;
import longbridge.services.RetailUserService;
import longbridge.services.ServiceReqConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Wunmi Sowunmi on 28/04/2017.
 */


@ControllerAdvice(basePackages = {"longbridge.controllers.retail"})
public class RetailControllerAdvice {

        @Autowired
        private RetailUserService retailUserService;

        @Autowired
        private ServiceReqConfigService reqConfigService;

        @ModelAttribute
        public String globalAttributes(Model model, Principal principal) {
            String greeting = "";

            if (principal.getName() == null){
                return "redirect:/login/retail";
            }


            RetailUser user = retailUserService.getUserByName(principal.getName());
            model.addAttribute("bvn", user.getBvn());

            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

            if(timeOfDay >= 0 && timeOfDay < 12){
                greeting = "Good morning, ";
            }else if(timeOfDay >= 12 && timeOfDay < 16){
                greeting = "Good afternoon, ";
            }else if(timeOfDay >= 16 && timeOfDay < 24){
                greeting = "Good evening, ";
            }
            model.addAttribute("greeting", greeting);

            String name = user.getFirstName() + ' ' + user.getLastName();
            model.addAttribute("name", name);

            List<ServiceReqConfig> requestList = reqConfigService.getServiceReqConfs();
            model.addAttribute("serviceRequests", requestList);

            //System.out.println( new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) );
            return "";
        }
}
