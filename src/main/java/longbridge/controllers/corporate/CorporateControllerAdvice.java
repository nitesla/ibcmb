package longbridge.controllers.corporate;

import longbridge.dtos.SettingDTO;
import longbridge.models.Account;
import longbridge.models.CorpUserType;
import longbridge.models.CorporateUser;
import longbridge.models.SRConfig;
import longbridge.services.*;
import longbridge.utils.DateFormatter;
import longbridge.utils.HostMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
@ControllerAdvice(basePackages = {"longbridge.controllers.corporate"})
public class CorporateControllerAdvice {

    @Autowired
    HostMaster hostMaster;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private CorporateUserService corporateUserService;
    private IntegrationService integrationService;
    private TransferService transferService;
    private AccountService accountService;
    private ServiceReqConfigService reqConfigService;
    private MessageService messageService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private  CorpTransferService corpTransferService;
    @Autowired
    private  BulkTransferService bulkTransferService;

    @Autowired
    public CorporateControllerAdvice(CorporateUserService corporateUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, ServiceReqConfigService reqConfigService, MessageService messageService) {
        this.corporateUserService = corporateUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.reqConfigService = reqConfigService;
        this.messageService = messageService;
    }

    @ModelAttribute
    public String globalAtrributes(Model model, Principal principal) {
        String greeting = "";
        if (principal.getName() == null) {
            return "redirect:/login/corporate";
        }

        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        String RCNumber;
        if (corporateUser.getCorporate().getRcNumber() == null) {
            RCNumber = "Not registered";
        } else {
            RCNumber = corporateUser.getCorporate().getRcNumber();
        }

        model.addAttribute("RcNo", RCNumber);

        String corporateName;
        if (corporateUser.getCorporate().getName() == null) {
            corporateName = "";
        } else {
            corporateName = corporateUser.getCorporate().getName();
        }

        model.addAttribute("compName", corporateName);

        if (corporateUser.getLastLoginDate() != null) {
            model.addAttribute("lastLogin", DateFormatter.format(corporateUser.getLastLoginDate()));
        } else {
            model.addAttribute("lastLogin", corporateUser.getLastLoginDate());
        }

        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Good morning, ";

        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = "Good afternoon, ";

        } else if (timeOfDay >= 16 && timeOfDay < 24) {
            greeting = "Good evening, ";
        }
        model.addAttribute("greeting", greeting);

        String firstName = corporateUser.getFirstName();
        String lastName = "";
        if (corporateUser.getLastName() == null) {
            lastName = "";

        } else {
            lastName = corporateUser.getLastName();
        }

        String name = firstName + ' ' + lastName;
        model.addAttribute("name", name);

        List<SRConfig> requestList = reqConfigService.getServiceReqConfs();
        model.addAttribute("serviceRequests", requestList);

        int numOfUnreadMessages = messageService.getNumOfUnreadMessages(corporateUser);
        if (numOfUnreadMessages > 0) {
            model.addAttribute("numOfUnreadMessages", numOfUnreadMessages);
        }

//        boolean isUserAdmin = corporateUser.isAdmin();
//        model.addAttribute("isUserAdmin",isUserAdmin);
        if (CorpUserType.ADMIN.equals(corporateUser.getCorpUserType())) {
            boolean isUserAdmin = true;
            model.addAttribute("isUserAdmin", isUserAdmin);
        }

        if (CorpUserType.AUTHORIZER.equals(corporateUser.getCorpUserType())) {
            boolean isAuthorizer = true;
            model.addAttribute("isAuthorizer", isAuthorizer);
        }

        if ("MULTI".equals(corporateUser.getCorporate().getCorporateType())){
            int pendingRequests = corpTransferService.countPendingRequest();
            if (pendingRequests > 0)
                model.addAttribute("pendingRequests", pendingRequests);

            int pendingBulk = bulkTransferService.getPendingBulkTransferRequests(corporateUser.getCorporate());
            if (pendingBulk > 0)
                model.addAttribute("pendingBulk", pendingBulk);

            int pending = pendingRequests + pendingBulk;
            model.addAttribute("pending", pending);
        }

        model.addAttribute("corporateType", corporateUser.getCorporate().getCorporateType());

        return "";
    }

    @ModelAttribute
    public String getCustmerAccounts(Model model, Principal principal) {

        if (principal == null || principal.getName() == null) {
            return "redirect:/login/corporate";
        }


        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        if (corporateUser != null) {
            List<Account> accountList = new ArrayList<>();

            Iterable<Account> accounts = accountService.getAccountsForDebit(corporateUser.getCorporate().getCustomerId());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .forEach(i -> accountList.add(i));


            model.addAttribute("accounts", accountList);
        }

        return "";
    }



//    @ModelAttribute
//    public void sessionTimeout(Model model) {
//        SettingDTO setting = configurationService.getSettingByName("SESSION_TIMEOUT");
//        try {
//            if (setting != null && setting.isEnabled()) {
//                Long timeOut = (Long.parseLong(setting.getValue()) * 60000) - 25000;
//                logger.info("SESSION TIME OUT PERIOD CORP" + timeOut);
//                model.addAttribute("timeOut", timeOut);
//            }
//
//        } catch (Exception ex) {
//        }
//    }


}
