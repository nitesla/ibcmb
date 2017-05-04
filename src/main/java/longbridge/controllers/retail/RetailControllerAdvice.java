package longbridge.controllers.retail;

import longbridge.dtos.TransferRequestDTO;
import longbridge.models.RetailUser;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import longbridge.services.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Wunmi Sowunmi on 28/04/2017.
 */


@ControllerAdvice(basePackages = {"longbridge.controllers.retail"})
public class RetailControllerAdvice {
    private RetailUserService retailUserService;
    private IntegrationService integrationService;
    private TransferService transferService;
    private AccountService accountService;

    @Autowired
    public RetailControllerAdvice(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
    }

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


            //System.out.println( new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) );
            return "";
        }

        @ModelAttribute
           public  String getCustmerAccounts(Model model,Principal principal){

            if (principal.getName() == null){
                return "redirect:/login/retail";
            }
            RetailUser user = retailUserService.getUserByName(principal.getName());
            if (user != null) {
                List<String> accountList = new ArrayList<>();

                integrationService.fetchAccounts(user.getCustomerId())
                        .stream()
                        .forEach(i -> accountList.add(i.getAccountNumber()));
                  model.addAttribute("accounts", accountList);




            }

            return "";
        }
}