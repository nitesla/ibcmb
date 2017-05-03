package longbridge.controllers.retail;



import longbridge.dtos.TransferRequestDTO;
import longbridge.models.RetailUser;
import longbridge.services.AccountService;
import longbridge.services.IntegrationService;
import longbridge.services.RetailUserService;
import longbridge.services.TransferService;
import longbridge.utils.TransferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Fortune on 4/3/2017.
 */
@Controller
@RequestMapping("/retail/transfer")
public class TransferController {



private RetailUserService retailUserService;
private IntegrationService integrationService;
private TransferService transferService;
private AccountService accountService;
private MessageSource messages;
    private LocaleResolver localeResolver;
    private LocalBeneficiaryService localBeneficiaryService;
    private FinancialInstitutionService financialInstitutionService;



    @Autowired
    public TransferController(RetailUserService retailUserService, IntegrationService integrationService, TransferService transferService, AccountService accountService, MessageSource messages, LocaleResolver localeResolver,LocalBeneficiaryService localBeneficiaryService,FinancialInstitutionService financialInstitutionService) {
        this.retailUserService = retailUserService;
        this.integrationService = integrationService;
        this.transferService = transferService;
        this.accountService = accountService;
        this.messages = messages;
        this.localeResolver = localeResolver;
        this.localBeneficiaryService=localBeneficiaryService;
        this.financialInstitutionService=financialInstitutionService;
    }






    @GetMapping
    public String getInternationalTransfer( Principal principal) throws Exception{
    return "cust/transfer/internationaltransfer/view";

    }

    @GetMapping("/coronationbanktransfer")
    public String getCoronationBankTransfer(Model model,Principal principal) throws Exception{
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        model.addAttribute("localBen", localBeneficiaryService.getLocalBeneficiaries(retailUser));

        return "cust/transfer/coronationbanktransfer/add";
    }
    @GetMapping("/{id}/coronation/maketransfer")
    public String makeCoronationtransfer(@PathVariable Long id, Model model, Principal principal) throws Exception {
        RetailUser retailUser = retailUserService.getUserByName(principal.getName());
        return "maketransfer";
    }

        @GetMapping("/coronationbank/new")
    public String addCoronationBeneficiary(Model model,LocalBeneficiaryDTO localBeneficiaryDTO) throws Exception{
        model.addAttribute("localBanks", financialInstitutionService.getFinancialInstitutionsByType(FinancialInstitutionType.LOCAL));

        return "cust/transfer/coronationbanktransfer/addbeneficiary";
    }
    @PostMapping("/coronationbank/new")
    public String createCoronationBeneficiary(@ModelAttribute("localBeneficiary") @Valid LocalBeneficiaryDTO localBeneficiaryDTO, Principal principal, BindingResult result, Model model) throws Exception{
        if(result.hasErrors()){
            return "cust/transfer/coronationbanktransfer/addbeneficiary";
        }

        RetailUser user = retailUserService.getUserByName(principal.getName());
        localBeneficiaryService.addLocalBeneficiary(user,localBeneficiaryDTO);
        model.addAttribute("success","Beneficiary added successfully");

        return "redirect:/retail/transfer/coronationbanktransfer";
    }

    @GetMapping("/interbanktransfer")
    public String getInterBank(Model model) throws Exception{
        return "cust/transfer/interbanktransfer/add";
    }

    @GetMapping("/ownaccount")
    public ModelAndView getOwnAccount(Principal principal) throws Exception {

        RetailUser user = retailUserService.getUserByName(principal.getName());
        if (user != null) {
            List<String> accountList = new ArrayList<>();

            integrationService.fetchAccounts(user.getCustomerId())

                    .stream()
                    .forEach(i -> accountList.add(i.getAccountNumber()));


            ModelAndView view = new ModelAndView();
            view.addObject("accounts", accountList);
          TransferRequestDTO  transferRequestDTO = new TransferRequestDTO();
            view.addObject("transferRequest",transferRequestDTO);
            view.setViewName("cust/transfer/ownaccount/transfer");
            return view;
        }


        throw new IllegalAccessException("");

    }


    @PostMapping("/ownaccount")
    public String makeOwnAccountTransfer(@ModelAttribute("transferRequestDTO") @Valid TransferRequestDTO transferRequestDTO, RedirectAttributes redirectAttributes,Locale locale) throws Exception{


       // boolean ok =transferService.makeTransfer(transferRequestDTO);
        boolean ok =true;

        if(ok)
        {
          // transferService.saveTransfer(transferRequestDTO);
          //  view.addObject("msg",messages.getMessage("transaction.success", null, locale));


        }



           redirectAttributes.addFlashAttribute("message",messages.getMessage("transaction.success", null, locale));

        return "redirect:/retail/transfer/ownaccount";

    }

    @GetMapping("/settransferlimit")
    public String getTransferLimit(Model model) throws Exception{
        return "cust/transfer/settransferlimit/view";
    }

         @PostMapping("/own/summary")
        public ModelAndView  ownTransferSummary(@ModelAttribute("transferRequest") @Valid TransferRequestDTO request){

            ModelAndView view= new ModelAndView();
            request.setBeneficiaryAccountName(integrationService.viewAccountDetails(request.getBeneficiaryAccountNumber()).getAcctName());
            request.setNarration("");//TODO A GENERIC WAY OF GENERATING THIS
             request.setReferenceNumber("");
             request.setDelFlag("N");
             request.setSessionId("");
             request.setTransferType(TransferType.OWN_ACCOUNT_TRANSFER);



            view.addObject("transferRequest",request);


            view.setViewName("cust/transfer/ownaccount/summary");



    return view;
        }

}
