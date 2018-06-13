package longbridge.controllers.retail;

import longbridge.dtos.NotificationsDTO;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.models.SRConfig;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * Created by Wunmi Sowunmi on 28/04/2017.
 */


@ControllerAdvice(basePackages = {"longbridge.controllers.retail"})
public class RetailControllerAdvice {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${bank.code}")
    private String bankCode;
    private RetailUserService retailUserService;
    private AccountService accountService;
    private ServiceReqConfigService reqConfigService;
    private MessageService messageService;
    private FinancialInstitutionService financialInstitutionService;
    private NotificationsService notificationsService;

    @Autowired
    public RetailControllerAdvice(RetailUserService retailUserService, AccountService accountService, ServiceReqConfigService reqConfigService, MessageService messageService
            , FinancialInstitutionService financialInstitutionService, NotificationsService notificationsService
    ) {
        this.retailUserService = retailUserService;
        this.accountService = accountService;
        this.reqConfigService = reqConfigService;
        this.messageService = messageService;
        this.financialInstitutionService = financialInstitutionService;
        this.notificationsService = notificationsService;
    }

    @ModelAttribute
    public String globalAttributes(Model model, Principal principal) {
        String greeting = "";

        if (principal == null) {
            return "redirect:/login/retail";
        }

        RetailUser user = retailUserService.getUserByName(principal.getName());
        String bvn = "";
        String lastLogin = "";
        if (user != null) {
            bvn = (user.getBvn() == null) ? "Not available" : user.getBvn();
            lastLogin = (user.getLastLoginDate() == null) ? DateFormatter.format(user.getCreatedOnDate()) : DateFormatter.format(user.getLastLoginDate());

            model.addAttribute("bvn", bvn);
            model.addAttribute("lastLogin", lastLogin);

            Calendar c = Calendar.getInstance();
            int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

            if (timeOfDay >= 0 && timeOfDay < 12) {
                greeting = "Good morning, ";
            } else if (timeOfDay >= 12 && timeOfDay < 16) {
                greeting = "Good afternoon, ";
            } else if (timeOfDay >= 16 && timeOfDay < 24) {
                greeting = "Good evening, ";
            }
            model.addAttribute("greeting", greeting);


            String firstName = user.getFirstName();
            String lastName = "";
            if (user.getLastName() == null) {
                lastName = "";

            } else {
                lastName = user.getLastName();
            }

            String name = firstName + ' ' + lastName;
            model.addAttribute("name", name);

            List<SRConfig> requestList = reqConfigService.getServiceReqConfs();
            model.addAttribute("serviceRequests", requestList);


            int numOfUnreadMessages = messageService.getNumOfUnreadMessages(user);
            if (numOfUnreadMessages > 0) {
                model.addAttribute("numOfUnreadMessages", numOfUnreadMessages);
            }
        }

        return "";
    }


    @ModelAttribute
    public String getCustomerAccounts(Model model, Principal principal) {


        if (principal == null || principal.getName() == null) {
            return "redirect:/login/retail";
        }


        RetailUser user = retailUserService.getUserByName(principal.getName());
        if (user != null) {
            List<Account> accountList = new ArrayList<>();

            Iterable<Account> accounts = accountService.getAccountsForDebit(user.getCustomerId());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .forEach(i -> accountList.add(i));


            model.addAttribute("accounts", accountList);
//            logger.info("fetch accounts from account table {} ",accountList);
        }

        return "";
    }

    @ModelAttribute
    public String getSystemNotifications(Model model) {
        try {
            List<NotificationsDTO> notifications = notificationsService.getNotifications();
//            NotificationsDTO notificationsDTO = new NotificationsDTO();
//            notificationsDTO.setMessage("welcome");
//            notifications.add(notificationsDTO);
            model.addAttribute("notifications", notifications);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }


        return "";
    }


//    @ModelAttribute
//    public String sessionTimeout(Model model) {
//        SettingDTO setting = configurationService.getSettingByName("SESSION_TIMEOUT");
//        try {
//            if (setting != null && setting.isEnabled()) {
//                Long timeOut = (Long.parseLong(setting.getValue()) * 60000) - 25000;
//                logger.info("SESSION TIME OUT PERIOD" + timeOut);
//                model.addAttribute("timeOut", timeOut);
//            }
//
//        }
//        catch (Exception ex) {
//        }
//
//        return "";
//
//    }


}
