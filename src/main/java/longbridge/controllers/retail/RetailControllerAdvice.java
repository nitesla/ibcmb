package longbridge.controllers.retail;

import longbridge.models.RetailUser;
import longbridge.services.RetailUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Calendar;

/**
 * Created by Wunmi Sowunmi on 28/04/2017.
 */
@ControllerAdvice(basePackages = {"longbridge.controllers.retail"})
public class RetailControllerAdvice {

        @Autowired
        private RetailUserService retailUserService;

        @ModelAttribute
        public void globalAttributes(Model model, Principal principal) {
            String greeting = "";
            RetailUser user = retailUserService.getUserByName(principal.getName());
            model.addAttribute("bvn", user.getBvn());



            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

            if(timeOfDay >= 0 && timeOfDay < 12){
                greeting = "Good morning, " + user.getFirstName() + " " + user.getLastName();
            }else if(timeOfDay >= 12 && timeOfDay < 16){
                greeting = "Good afternoon, " + user.getFirstName() + " " + user.getLastName();
            }else if(timeOfDay >= 16 && timeOfDay < 24){
                greeting = "Good evening, " + user.getFirstName() + " " + user.getLastName();
            }
            model.addAttribute("greeting", greeting);


            //System.out.println( new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) );

        }
}
