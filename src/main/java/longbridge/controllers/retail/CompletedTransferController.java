package longbridge.controllers.retail;

import longbridge.models.RetailUser;
import longbridge.models.TransRequest;
import longbridge.services.RetailUserService;
import longbridge.services.TransferService;
import longbridge.utils.DateFormatter;
import longbridge.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Showboy on 12/08/2017.
 */
@Controller
@RequestMapping("/retail/transfer")
public class
CompletedTransferController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    TransferService transferService;

    @Autowired
    RetailUserService retailUserService;

    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private MessageSource messageSource;

    @Value("${jrxmlImage.path}")
    private String imagePath;
    @Value("${jsonFile.path}")
    private String JOSNpath;
    @Value("${jrxmlFile.path}")
    private String jrxmlPath;
    @Value("${savedDocFile.path}")
    private String savedDoc;

    @GetMapping("/history")
    public String completedTransfers(){
        return "cust/transfer/completed";
    }

    @GetMapping("/history/all")
    public @ResponseBody
    DataTablesOutput<TransRequest> getTransfersCompleted(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);

        Page<TransRequest> transferRequests = transferService.getCompletedTransfers(pageable);
//        if (StringUtils.isNoneBlank(search)) {
//            transferRequests = transferService.getCompletedTransfers(search, pageable);
//        }else{
//            transferRequests = transferService.getCompletedTransfers(pageable);
//        }
        DataTablesOutput<TransRequest> out = new DataTablesOutput<TransRequest>();
        out.setDraw(input.getDraw());
        out.setData(transferRequests.getContent());
        out.setRecordsFiltered(transferRequests.getTotalElements());
        out.setRecordsTotal(transferRequests.getTotalElements());

        return out;
    }

    @RequestMapping(path = "{id}/downloadreceipt", method = RequestMethod.GET)
    public ModelAndView getTransPDF(@PathVariable Long id, Model model, Principal principal, HttpServletRequest request) {
        try {
            RetailUser retailUser = retailUserService.getUserByName(principal.getName());

            TransRequest transRequest = transferService.getTransfer(id);

//            logger.info("Trans Request {}", transRequest);
            JasperReportsPdfView view = new JasperReportsPdfView();
            view.setUrl("classpath:jasperreports/rpt_tran-hist.jrxml");
            view.setApplicationContext(appContext);

            Map<String, Object> modelMap = new HashMap<>();
            double amount = Double.parseDouble(transRequest.getAmount().toString());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            modelMap.put("datasource", new ArrayList<>());
            modelMap.put("imagePath", imagePath);
            modelMap.put("amount", formatter.format(amount));
            modelMap.put("customer",retailUser.getFirstName()+" "+retailUser.getLastName() );
            modelMap.put("customerAcctNumber", StringUtil.maskAccountNumber(transRequest.getCustomerAccountNumber()));
            if(transRequest.getRemarks() != null) {
                modelMap.put("remarks", transRequest.getRemarks());
            }else{
                modelMap.put("remarks", "");
            }
            modelMap.put("beneficiary", transRequest.getBeneficiaryAccountName());
            modelMap.put("beneficiaryAcctNumber", transRequest.getBeneficiaryAccountNumber());
            modelMap.put("beneficiaryBank", transRequest.getFinancialInstitution().getInstitutionName());
            modelMap.put("refNUm", transRequest.getReferenceNumber());
            modelMap.put("tranDate", DateFormatter.format(transRequest.getTranDate()));
            modelMap.put("date", DateFormatter.format(new Date()));

            ModelAndView modelAndView=new ModelAndView(view, modelMap);
            return modelAndView;
        }catch (Exception e){
            logger.info(" RECEIPT DOWNLOAD {} ", e.getMessage());
            ModelAndView modelAndView =  new ModelAndView("redirect:/retail/transfer/history");
            modelAndView.addObject("failure", messageSource.getMessage("receipt.download.failed", null, locale));
            //redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("receipt.download.failed", null, locale));
            return modelAndView;
        }

    }
}
