package longbridge.controllers.corporate;

import longbridge.models.CorpTransRequest;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.TransRequest;
import longbridge.services.CorpTransferService;
import longbridge.services.CorporateUserService;
import longbridge.services.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Showboy on 12/08/2017.
 */
@Controller
@RequestMapping("/corporate/transfer")
public class CorpCompletedTransferController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CorporateUserService corporateUserService;
    @Autowired
    TransferService transferService;
    @Autowired
    private ApplicationContext appContext;

    @Value("${jsonFile.path}")
    private String JOSNpath;
    @Value("${jrxmlFile.path}")
    private String jrxmlPath;
    @Value("${savedDocFile.path}")
    private String savedDoc;
    @Value("${excel.path}")
    String PROPERTY_EXCEL_SOURCE_FILE_PATH;

    @Autowired
    CorpTransferService corpTransferService;

    @GetMapping("/history")
    public String completedTransfers(){
        return "corp/transfer/completed";
    }

    @GetMapping("/history/all")
    public @ResponseBody
    DataTablesOutput<CorpTransRequest> getTransfersCompleted(DataTablesInput input){

        Pageable pageable = DataTablesUtils.getPageable(input);

        Page<CorpTransRequest> transferRequests = corpTransferService.getCompletedTransfers(pageable);
//        if (StringUtils.isNoneBlank(search)) {
//            transferRequests = transferService.findCompletedTransfers(search, pageable);
//        }else{
//            transferRequests = transferService.getCompletedTransfers(pageable);
//        }
        DataTablesOutput<CorpTransRequest> out = new DataTablesOutput<CorpTransRequest>();
        out.setDraw(input.getDraw());
        out.setData(transferRequests.getContent());
        out.setRecordsFiltered(transferRequests.getTotalElements());
        out.setRecordsTotal(transferRequests.getTotalElements());

        return out;
    }

    @RequestMapping(path = "{id}/downloadreceipt", method = RequestMethod.GET)
    public ModelAndView getTransPDF(@PathVariable Long id, Model model, Principal principal, HttpServletRequest request) {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        Corporate corporate = corporateUser.getCorporate();
        TransRequest transRequest = transferService.getTransfer(id);

        logger.info("Trans Request {}", transRequest);
        JasperReportsPdfView view = new JasperReportsPdfView();
        view.setUrl("classpath:jasperreports/rpt_tran-hist.jrxml");
        view.setApplicationContext(appContext);

        Map<String, Object> modelMap = new HashMap<>();
            double amount = Double.parseDouble(transRequest.getAmount().toString());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            modelMap.put("datasource", new ArrayList<>());
            modelMap.put("amount", formatter.format(amount));
            modelMap.put("sender",corporate.getName());
            modelMap.put("remarks", transRequest.getRemarks());
            modelMap.put("recipientBank", transRequest.getFinancialInstitution().getInstitutionName());
            modelMap.put("beneficiary", transRequest.getBeneficiaryAccountName());
            modelMap.put("beneficiaryAccountNumber", transRequest.getBeneficiaryAccountNumber());
            modelMap.put("refNUm", transRequest.getReferenceNumber());
            //modelMap.put("tranDate", DateFormatter.format(transRequest.getTranDate()));


        ModelAndView modelAndView=new ModelAndView(view, modelMap);
        return modelAndView;
    }
}