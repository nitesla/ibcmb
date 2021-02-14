package longbridge.controllers.corporate;

import longbridge.models.Account;
import longbridge.models.CorpUserType;
import longbridge.models.CorporateUser;
import longbridge.servicerequests.config.RequestConfig;
import longbridge.servicerequests.config.RequestConfigInfo;
import longbridge.servicerequests.config.RequestConfigService;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * Created by SYLVESTER on 5/22/2017.
 */
@ControllerAdvice(basePackages = {"longbridge.controllers.corporate"})
public class CorporateControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CorporateUserService corporateUserService;
    private final AccountService accountService;
    private final RequestConfigService reqConfigService;
    private final MessageService messageService;
    @Autowired
    HostMaster hostMaster;
    @Autowired
    private SettingsService configurationService;
    @Autowired
    private CorpTransferService corpTransferService;
    @Autowired
    private BulkTransferService bulkTransferService;
    @Autowired
    private CorpUserVerificationService corpUserVerificationService;

    @Autowired
    public CorporateControllerAdvice(CorporateUserService corporateUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, RequestConfigService reqConfigService, MessageService messageService) {
        this.corporateUserService = corporateUserService;
        this.accountService = accountService;
        this.reqConfigService = reqConfigService;
        this.messageService = messageService;
    }

    @ModelAttribute
    public String globalAtrributes(Model model, Principal principal) {
        String greeting = "";
        if (principal == null || principal.getName() == null) {
            return "redirect:/login/corporate";
        }

        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        logger.info("corporateUser", corporateUser);
        if (corporateUser == null) {
            return "";
        }

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
        model.addAttribute("feedStatus", corporateUser.getFeedBackStatus());

        List<RequestConfigInfo> requestList = reqConfigService.getRequestConfigs();
        List<RequestConfigInfo> filteredRequests = new ArrayList<>();
        List<RequestConfigInfo> chequeRequests = new ArrayList<>();
        List<RequestConfigInfo> settingRequests = new ArrayList<>();

        for (RequestConfigInfo request : requestList) {
            if (!request.isSystem()) {
                filteredRequests.add(request);
            } else if (("CHEQUE").equalsIgnoreCase(request.getName().trim())) {
                chequeRequests.add(request);
            } else if (("SETTING").equalsIgnoreCase(request.getName().trim())) {
                settingRequests.add(request);
            }
        }
        model.addAttribute("serviceRequests", filteredRequests);
        model.addAttribute("chequeRequests", chequeRequests);
        model.addAttribute("settingRequests", settingRequests);


        int numOfUnreadMessages = messageService.getNumOfUnreadMessages(corporateUser);
        if (numOfUnreadMessages > 0) {
            model.addAttribute("numOfUnreadMessages", numOfUnreadMessages);
        }
        if (CorpUserType.ADMIN.equals(corporateUser.getCorpUserType())) {
            boolean isUserAdmin = true;
            model.addAttribute("isUserAdmin", isUserAdmin);
        }

        if (CorpUserType.AUTHORIZER.equals(corporateUser.getCorpUserType())) {
            boolean isAuthorizer = true;
            model.addAttribute("isAuthorizer", isAuthorizer);
        }

        if ("MULTI".equals(corporateUser.getCorporate().getCorporateType())) {
            int pendingRequests = corpTransferService.countPendingRequest();
            if (pendingRequests > 0)
                model.addAttribute("pendingRequests", pendingRequests);

            int pendingBulk = bulkTransferService.getPendingBulkTransferRequests(corporateUser.getCorporate());
            if (pendingBulk > 0)
                model.addAttribute("pendingBulk", pendingBulk);

            int pending = pendingRequests + pendingBulk;
            model.addAttribute("pending", pending);

            int pendingVer = corpUserVerificationService.getTotalNumberPending();
            if (pendingVer > 0)
                model.addAttribute("corpPendingVerification", pendingVer);
        }

        model.addAttribute("corporateType", corporateUser.getCorporate().getCorporateType());

        return "";
    }

    @ModelAttribute
    public String getCustomerAccounts(Model model, Principal principal) {

        if (principal == null || principal.getName() == null) {
            return "redirect:/login/corporate";
        }


        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());
        if (corporateUser != null) {
            List<Account> accountList = new ArrayList<>();

            Iterable<Account> accounts = accountService.getAccountsForDebit(corporateUser.getCorporate().getAccounts());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .forEach(accountList::add);

            model.addAttribute("accounts", accountList);

            List<Account> accountListStmt = new ArrayList<>();

            Iterable<Account> accountsStmt = accountService.getAccountsForDebitForStatement(corporateUser.getCorporate().getAccounts());

            StreamSupport.stream(accounts.spliterator(), false)
                    .filter(Objects::nonNull)
                    .forEach(accountListStmt::add);
            model.addAttribute("accountsStmt", accountsStmt);
        }

        return "";
    }
}
