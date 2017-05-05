package longbridge.controllers;

import longbridge.api.CustomerDetails;
import longbridge.dtos.RetailUserDTO;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by Wunmi Sowunmi on 18/04/2017.
 */
@Controller
public class UserRegController {

    @Autowired
    private IntegrationService integrationService;
    
    @Autowired
    private RetailUserService retailUserService;

    private Logger logger= LoggerFactory.getLogger(this.getClass());
    @GetMapping("/register")
    public String registerPage(){
        return "cust/register/registration";
    }

    @PostMapping("/register")
    public String addUser(@ModelAttribute("requestForm") RetailUserDTO retailUserDTO, BindingResult result, Model model, WebRequest webRequest){
        if(result.hasErrors()){
            return "cust/servicerequest/add";
        }

        logger.info(retailUserDTO.toString());
        String accountNumber = webRequest.getParameter("acct");
        String email = webRequest.getParameter("email");
        String dob = webRequest.getParameter("birthDate");

        CustomerDetails details = integrationService.isAccountValid(accountNumber, email, dob);
        retailUserService.addUser(retailUserDTO, details);
        model.addAttribute("success", "Registration successful");
        return "redirect:/retail/requests";
    }
    
    @GetMapping("/rest/json/phishingimages")
    public @ResponseBody String antiPhishingImages(){
    	StringBuilder builder = new StringBuilder();
    	builder.append("<option value=''>Select Anti Phishing Image</option>");
    	builder.append("<option value='/assets/phishing/dog.jpg'>Dog</option>");
    	builder.append("<option value='/assets/phishing/cheetah.jpg'>Cheetah</option>");
    	builder.append("<option value='/assets/phishing/benz.jpg'>Car</option>");
    	return builder.toString();
    }

    @GetMapping("/rest/accountname/{accountNumber}")
    public @ResponseBody String getAccountNameFromNumber(@PathVariable String accountNumber){
    	logger.info("Account nUmber : " + accountNumber);
        return integrationService.getAccountName(accountNumber);
    }

    @GetMapping("/rest/username/check/{username}")
    public @ResponseBody String checkUsername(@PathVariable String username){
        return "true";
    }

}
