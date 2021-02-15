package longbridge.controllers.corporate;

import longbridge.dtos.CorpTransferRequestDTO;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
import longbridge.models.TransRequest;
import longbridge.services.CorpTransferService;
import longbridge.services.CorporateUserService;
import longbridge.services.TransferService;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.DateFormatter;
import longbridge.utils.JasperReport.ReportHelper;
import longbridge.utils.StringUtil;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Showboy on 12/08/2017.
 */
@Controller
@RequestMapping("/corporate/transfer")
public class CorpCompletedTransferController {

    @Autowired
    TransferService transferService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Locale locale = LocaleContextHolder.getLocale();
    @Autowired
    private CorporateUserService corporateUserService;
    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private MessageSource messageSource;

    @Value("${report.logo.url}")
    private String imagePath;
    @Value("${jsonFile.path}")
    private String JOSNpath;
    @Value("${account-statement.template.name}")
    private String jrxmlPath;
    @Value("${savedDocFile.path}")
    private String savedDoc;

    @Autowired
    private CorpTransferService corpTransferService;

    @GetMapping("/history")
    public String completedTransfers() {
        return "corp/transfer/completed";
    }

    @GetMapping("/history/all")
    public @ResponseBody
    DataTablesOutput<CorpTransferRequestDTO> getTransfersCompleted(DataTablesInput input, @RequestParam("csearch") String search) {

        Pageable pageable = DataTablesUtils.getPageable(input);

        Page<CorpTransferRequestDTO> transferRequests;
        if (StringUtils.isNoneBlank(search)) {

            transferRequests = corpTransferService.getCompletedTransfer(search.toUpperCase(), pageable);
        } else {

            transferRequests = corpTransferService.getCompletedTransfer(pageable);
        }
        DataTablesOutput<CorpTransferRequestDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(transferRequests.getContent());
        out.setRecordsFiltered(transferRequests.getTotalElements());
        out.setRecordsTotal(transferRequests.getTotalElements());
        return out;
    }

    @RequestMapping(path = "{id}/downloadreceipt", method = RequestMethod.GET)
    public ModelAndView getTransPDF(@PathVariable Long id, Model model, Principal principal, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws Exception {
        try {
            CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

            Corporate corporate = corporateUser.getCorporate();
            TransRequest transRequest = transferService.getTransfer(id);


            Map<String, Object> modelMap = new HashMap<>();
            double amount = Double.parseDouble(transRequest.getAmount().toString());
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            modelMap.put("datasource", new ArrayList<>());
            modelMap.put("imagePath", imagePath);
            modelMap.put("amount", formatter.format(amount));
//        modelMap.put("customer", corporate.getName());
            modelMap.put("customer", corporateUser.getFirstName() + " " + corporateUser.getLastName());
            modelMap.put("customerAcctNumber", StringUtil.maskAccountNumber(transRequest.getCustomerAccountNumber()));
            if (transRequest.getRemarks() != null) {
                modelMap.put("remarks", transRequest.getRemarks());
            } else {
                modelMap.put("remarks", "");
            }
            modelMap.put("beneficiary", transRequest.getBeneficiaryAccountName());
            modelMap.put("beneficiaryAcctNumber", transRequest.getBeneficiaryAccountNumber());
            modelMap.put("beneficiaryBank", transRequest.getBeneficiaryBank());
//            modelMap.put("beneficiaryBank", transRequest.getFinancialInstitution().getInstitutionName());
            modelMap.put("refNUm", transRequest.getReferenceNumber());
            modelMap.put("tranDate", DateFormatter.format(transRequest.getTranDate()));
            modelMap.put("date", DateFormatter.format(new Date()));
//            modelMap.put("status", transRequest.getStatusDescription());

            if ("00".equals(transRequest.getStatus()) || "000".equals(transRequest.getStatus()))
                modelMap.put("statusDescription", "Transaction Successful");
            else if ("09".equals(transRequest.getStatus()) || "34".equals(transRequest.getStatus()))
                modelMap.put("statusDescription", "Pending");
            else modelMap.put("statusDescription", "Failed");

            JasperReport jasperReport = ReportHelper.getJasperReport("rpt_tran-hist");

            response.setContentType("application/x-download");
            response.setHeader("Content-Disposition", "attachment; filename=\"tran-hist.pdf\"");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap);
            JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
        } catch (Exception e) {
            logger.info(" RECEIPT DOWNLOAD {} ", e.getMessage());

        }
        ModelAndView modelAndView=new ModelAndView("redirect:/corp/dashboard");
        modelAndView.addObject("failure", messageSource.getMessage("receipt.download.failed", null, locale));
        return modelAndView;


    }
}


