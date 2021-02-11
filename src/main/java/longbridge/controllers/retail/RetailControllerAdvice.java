package longbridge.controllers.retail;

import longbridge.dtos.NotificationsDTO;
import longbridge.models.Account;
import longbridge.models.RetailUser;
import longbridge.servicerequests.config.RequestConfig;
import longbridge.servicerequests.config.RequestConfigService;
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
import java.util.*;
import java.util.stream.StreamSupport;


@ControllerAdvice(basePackages = {"longbridge.controllers.retail"})
public class RetailControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RetailUserService retailUserService;
    private final AccountService accountService;
    private final RequestConfigService reqConfigService;
    private final MessageService messageService;
    private final NotificationsService notificationsService;
    @Value("${bank.code}")
    private String bankCode;

    @Autowired
    public RetailControllerAdvice(RetailUserService retailUserService, AccountService accountService, RequestConfigService reqConfigService, MessageService messageService
            , FinancialInstitutionService financialInstitutionService, NotificationsService notificationsService
    ) {
        this.retailUserService = retailUserService;
        this.accountService = accountService;
        this.reqConfigService = reqConfigService;
        this.messageService = messageService;
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
            model.addAttribute("feedStatus", user.getFeedBackStatus());

            List<RequestConfig> requestList = reqConfigService.getRequestConfigs();
            List<RequestConfig> filteredRequests = new ArrayList<>();
            List<RequestConfig> chequeRequests = new ArrayList<>();
            List<RequestConfig> settingRequests = new ArrayList<>();
            List<RequestConfig> accountRequests = new ArrayList<>();

            for (RequestConfig request : requestList) {
                if (!request.isSystem()) {
                    filteredRequests.add(request);
                } else if (("CHEQUE").equalsIgnoreCase(request.getName().trim())) {
                    chequeRequests.add(request);
                } else if (("SETTING").equalsIgnoreCase(request.getName().trim())) {
                    settingRequests.add(request);
                } else if (("ACCOUNT").equalsIgnoreCase(request.getName().trim())) {
                    accountRequests.add(request);
                }
            }
            model.addAttribute("serviceRequests", filteredRequests);
            model.addAttribute("chequeRequests", chequeRequests);
            model.addAttribute("settingRequests", settingRequests);
            model.addAttribute("accountRequests", accountRequests);
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
                    .forEach(accountList::add);
            model.addAttribute("accounts", accountList);
        }

        return "";
    }

    @ModelAttribute
    public String getSystemNotifications(Model model) {
        try {
            List<NotificationsDTO> notifications = notificationsService.getNotifications();
            model.addAttribute("notifications", notifications);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return "";
    }


}
