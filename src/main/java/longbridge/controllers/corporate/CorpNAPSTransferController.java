package longbridge.controllers.corporate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.api.NEnquiryDetails;
import longbridge.dtos.*;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.services.*;
import longbridge.services.bulkTransfers.TransferStatusJobLauncher;
import longbridge.utils.*;
import longbridge.utils.JasperReport.ReportHelper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.security.Principal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 5/30/2017.
 */

@Controller
@RequestMapping("/corporate/transfer")
public class CorpNAPSTransferController {

    private static final String FILENAME = "bulk_transfer_upload_file.xls";
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    IntegrationService integrationService;
    //private static final String SERVER_FILE_PATH = "C:\\ibanking\\files\\Copy-of-NEFT-ECOB-ABC-old-mutual.xls";
    @Autowired
    private MessageSource messageSource;
    private final AccountService accountService;
    private final CorporateUserService corporateUserService;
    private final CorporateService corporateService;
    private final BulkTransferService bulkTransferService;
    private final SecurityService securityService;
    private final FinancialInstitutionService financialInstitutionService;
    @Autowired
    private SettingsService configService;
    @Autowired
    private TransferErrorService transferErrorService;
    @Autowired
    private TransferUtils transferUtils;
    @Autowired
    private ApplicationContext appContext;
    @Value("${report.logo.url}")
    private String imagePath;
    @Value("${jrxmlBulkExcelFile.path}")
    private String jrxmlBulkExcelFile;
    @Autowired
    private TransferStatusJobLauncher transferStatusJobLauncher;

    @Autowired
    public CorpNAPSTransferController(AccountService accountService, CorporateUserService corporateUserService,
                                      BulkTransferService bulkTransferService, SecurityService securityService,
                                      FinancialInstitutionService financialInstitutionService, CorpTransferService corpTransferService,
                                      CorporateService corporateService) {
        this.accountService = accountService;
        this.corporateUserService = corporateUserService;
        this.bulkTransferService = bulkTransferService;
        this.securityService = securityService;
        this.financialInstitutionService = financialInstitutionService;
        this.corporateService = corporateService;
    }

//    @GetMapping("/bulk")
//    public String getBulkTransfers(Model model, HttpSession session) {
//        if (session.getAttribute("workbook") != null) {
//            session.removeAttribute("workbook");
//        }
//        return "/corp/transfer/bulktransfer/list";
//    }

    @GetMapping("/bulk")
    public String getBulkTransfers(Model model) {
        return "/corp/transfer/bulktransfer/pagei";
    }

    @GetMapping("/upload")
    public String getUploadBulkTransferFile(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        try {
            transferUtils.validateTransferCriteria();
        } catch (InternetBankingTransferException e) {
            String errorMessage = transferErrorService.getMessage(e);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/corporate/transfer/bulk";
        }

        if (session.getAttribute("workbook") != null) {
            session.removeAttribute("workbook");
        }
        return "/corp/transfer/bulktransfer/upload";
    }


    @GetMapping("/{id}/view")
    public String getBulkTransferCreditRequests(@PathVariable Long id, Model model, Principal principal) {
        BulkTransfer bulkTransfer = bulkTransferService.getBulkTransferRequest(id);
        model.addAttribute("bulkTransfer", bulkTransfer);
        if (principal == null || principal.getName() == null) {
            return "redirect:/login/corporate";
        }
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Corporate corporate = user.getCorporate();
        if (corporate.getCorporateType().equalsIgnoreCase("MULTI")) {
            CorpTransferAuth corpTransferAuth = bulkTransferService.getAuthorizations(bulkTransfer);
            CorpTransRule corpTransRule = corporateService.getApplicableTransferRule(bulkTransfer);
            boolean userCanAuthorize = bulkTransferService.userCanAuthorize(bulkTransfer);
            model.addAttribute("authorizationMap", corpTransferAuth);
            model.addAttribute("corpTransRequest", bulkTransfer);
            model.addAttribute("corpTransReqEntry", new CorpTransReqEntry());
            model.addAttribute("corpTransRule", corpTransRule);
            model.addAttribute("userCanAuthorize", userCanAuthorize);

            List<CorporateRole> rolesNotInAuthList = new ArrayList<>();
            List<CorporateRole> rolesInAuth = new ArrayList<>();

            if (corpTransferAuth.getAuths() != null) {
                for (CorpTransReqEntry transReqEntry : corpTransferAuth.getAuths()) {
                    rolesInAuth.add(transReqEntry.getRole());
                }
            }

            if (corpTransRule != null) {
                for (CorporateRole role : corpTransRule.getRoles()) {
                    if (!rolesInAuth.contains(role)) {
                        rolesNotInAuthList.add(role);
                    }
                }
            }
            logger.debug("Roles not In Auth List..{}", rolesNotInAuthList.toString());
            model.addAttribute("rolesNotAuth", rolesNotInAuthList);

            return "corp/transfer/bulktransfer/approvemulti";
        } else if (corporate.getCorporateType().equalsIgnoreCase("SOLE")) {
            return "/corp/transfer/bulktransfer/crlistview";
        } else {
            return "redirect:/login/corporate";
        }
    }

    @PostMapping("/bulk/approve")
    public String addBulkTransferAuthorization(@ModelAttribute("corpTransReqEntry") CorpTransReqEntry corpTransReqEntry, @RequestParam("token") String tokenCode, RedirectAttributes redirectAttributes, Principal principal, Locale locale) {
        corpTransReqEntry.setChannel("INTERNET");

        CorporateUser user = corporateUserService.getUserByName(principal.getName());


        SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");

        if (setting != null && setting.isEnabled()) {

            if (tokenCode != null && !tokenCode.isEmpty()) {
                try {
                    boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), tokenCode);
                    if (!result) {
                        logger.error("Error authenticating token");
                        redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("token.auth.failure", null, locale));
                        return "redirect:/corporate/transfer/" + corpTransReqEntry.getTranReqId() + "/view";
                    }
                } catch (InternetBankingSecurityException se) {
                    logger.error("Error authenticating token");
                    redirectAttributes.addFlashAttribute("failure", se.getMessage());
                    return "redirect:/corporate/transfer/" + corpTransReqEntry.getTranReqId() + "/view";
                }
            } else {
                redirectAttributes.addFlashAttribute("failure", "Token code is required");
                return "redirect:/corporate/transfer/" + corpTransReqEntry.getTranReqId() + "/view";

            }
        }


        try {
            logger.debug("corpTransferReqEntry:{}", corpTransReqEntry);
            String message = bulkTransferService.addAuthorization(corpTransReqEntry);
            redirectAttributes.addFlashAttribute("message", message);

        } catch (InternetBankingException ibe) {
            logger.error("Failed to authorize transfer", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());

        }
        return "redirect:/corporate/transfer/bulk";
    }


    @GetMapping("/add")
    public String addBulkTransfer(Model model) {
        return "/corp/transfer/bulktransfer/add";
    }

    @GetMapping("/bankcodes")
    public String veiwSortCodes(Model model) {
        return "/corp-bankcodes";
    }

    @GetMapping(path = "/allbankcodes")
    public
    @ResponseBody
    DataTablesOutput<FinancialInstitutionDTO> getAllFis(DataTablesInput input, @RequestParam("csearch") String search) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<FinancialInstitutionDTO> fis = null;
        if (StringUtils.isNoneBlank(search)) {
            fis = financialInstitutionService.getFinancialInstitutionsWithSortCode(search,pageable);
        }else {
            fis = financialInstitutionService.getFinancialInstitutionsWithSortCode(pageable);
        }
        DataTablesOutput<FinancialInstitutionDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(fis.getContent());
        out.setRecordsFiltered(fis.getTotalElements());
        out.setRecordsTotal(fis.getTotalElements());
        return out;
    }


    @GetMapping("/bulk/download")
    public String downloadFile(HttpServletResponse response, Model model, Locale locale) throws IOException {
        File file = null;
//        file = new File(SERVER_FILE_PATH);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        file = new File(classloader.getResource(FILENAME).getFile().replaceAll("%20", " "));
        logger.info("download file {}", file.getName());
        logger.info("download file {}", file.getAbsolutePath());
        if (!file.exists()) {
            model.addAttribute("failure", messageSource.getMessage("file.not.found", null, locale));
            return "/corp/transfer/bulktransfer/upload";
        }
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        response.setContentLength((int) file.length());
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        //Copy bytes from source to destination(outputstream in this example), closes both streams.
        FileCopyUtils.copy(inputStream, response.getOutputStream());
        return null;
    }


    @PostMapping("/bulk/upload")
    public String uploadBulkTransferFile(@RequestParam("transferFile") MultipartFile file, Principal principal, RedirectAttributes redirectAttributes, Model model, Locale locale, HttpSession httpSession) throws IOException {

        if (principal == null || principal.getName() == null) {
            return "redirect:/login/corporate";
        }
        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        List<String> accountList = new ArrayList<>();
        if (user != null) {

            Iterable<Account> accountNumbers = accountService.getAccountsForDebit(user.getCorporate().getAccounts());

            StreamSupport.stream(accountNumbers.spliterator(), false)
                    .filter(Objects::nonNull)
                    .forEach(i -> accountList.add(i.getAccountNumber()));
            model.addAttribute("accounts", accountList);
        }

        if (file.isEmpty()) {
            model.addAttribute("failure", messageSource.getMessage("file.require", null, locale));
            return "/corp/transfer/bulktransfer/upload";
        }


        // Get the file, perform some validations and save it in a session
        try {
            byte[] bytes = file.getBytes();
            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            String extension = filename.substring(filename.lastIndexOf(".") + 1);
            // File type validation
            Workbook workbook;
            if (extension.equalsIgnoreCase("xls")) {

                workbook = new HSSFWorkbook(inputStream);

            } else if (extension.equalsIgnoreCase("xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else {

                model.addAttribute("failure", messageSource.getMessage("file.format.failure", null, locale));
                return "/corp/transfer/bulktransfer/upload";
            }

            // File content validation using number of header cells
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            if (iterator.hasNext()) {
                Row headerRow = iterator.next();
                if (headerRow.getLastCellNum() > 5) {
                    model.addAttribute("failure", messageSource.getMessage("file.content.failure", null, locale));
                    return "/corp/transfer/bulktransfer/upload";
                }
            }
            //File Account numbers validation
           /* for(Row row:datatypeSheet){
                row.createCell(5);
                if(row.getRowNum()!=0) {
                    NEnquiryDetails details = integrationService.doNameEnquiry(row.getCell(4).toString(), row.getCell(0).toString());
                    if(details.getAccountName()!=null && !details.getAccountName().equals("")) {
                        row.getCell(5).setCellValue(details.getAccountName());
                        logger.info("NameEnquiry" + row.getCell(5));
                    }else {
                        row.getCell(5).setCellValue(messageSource.getMessage("nameEnquiry.failed", null, locale));
                        logger.info("NameEnquiry" + row.getCell(5));
                    }

                }else{
                    row.getCell(5).setCellValue("Account Name");
                }
            }*/
            httpSession.setAttribute("accountList", accountList);
            httpSession.setAttribute("workbook", workbook);
            httpSession.setAttribute("fileExtension", extension);
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("file.upload.success", null, locale));
        } catch (IOException e) {
            logger.error("Error uploading file", e);

            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("file.upload.failure", null, locale));
            return "redirect:/corporate/transfer/upload";
        }

        return "/corp/transfer/bulktransfer/add";
    }


    @GetMapping("/all")
    @ResponseBody
    public DataTablesOutput<CreditRequestDTO> getCreditRequests(HttpSession session, Model model, Locale locale) throws IOException {
        Workbook workbook = (Workbook) session.getAttribute("workbook");
        String fileExtension = (String) session.getAttribute("fileExtension");
        model.addAttribute("accountList", session.getAttribute("accountList"));

        List<CreditRequestDTO> crLists = new ArrayList<>();
        try {
            System.out.println(workbook);

            Sheet datatypeSheet = workbook.getSheetAt(0);

            for (Row row : datatypeSheet) {
                row.createCell(5);
                if (row.getRowNum() != 0) {
                    NEnquiryDetails details = integrationService.doNameEnquiry(row.getCell(4).toString(), row.getCell(0).toString());
                    if (details.getAccountName() != null && !details.getAccountName().equals("")) {
                        row.getCell(5).setCellValue(details.getAccountName());
                    } else {
                        row.getCell(5).setCellValue(messageSource.getMessage("nameEnquiry.failed", null, locale));
                    }
                    logger.info("NameEnquiry" + row.getCell(5));

                } else {
                    row.getCell(5).setCellValue("Account Name");
                }
            }

            Iterator<Row> iterator = datatypeSheet.iterator();

            if (iterator.hasNext()) iterator.next();


            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                //Iterator<Cell> cellIter ator = currentRow.iterator();
                ArrayList cellData = new ArrayList();
                for (int i = 0; i < 6; i++) {
                    Cell currentCell = currentRow.getCell(i);
                    System.out.println(currentCell);
                    if (currentCell == null || currentCell.getCellType() == CellType.BLANK || currentCell.toString().isEmpty() || currentCell.toString() == null) {
                        cellData.add("ERROR: Empty cell");
                    } else {
                        cellData.add(currentCell);
                    }


                }

                int rowIndex = currentRow.getRowNum();
                CreditRequestDTO creditRequest = new CreditRequestDTO();
                Long id = (long) rowIndex;
                creditRequest.setId(id);

                if (!(NumberUtils.isDigits(cellData.get(0).toString())) && !(cellData.get(0).toString().contains("ERROR"))) {
                    creditRequest.setAccountNumber("ERROR: Not a number");
                } else {
                    creditRequest.setAccountNumber(cellData.get(0).toString());
                }

                creditRequest.setAccountName(cellData.get(1).toString());

                if (!(NumberUtils.isNumber(cellData.get(2).toString())) && !(cellData.get(2).toString().contains("ERROR"))) {
                    creditRequest.setAmount(new BigDecimal(-1));
                } else {
                    creditRequest.setAmount(new BigDecimal(cellData.get(2).toString()));
                }

                creditRequest.setNarration(cellData.get(3).toString());
                creditRequest.setAccountNameEnquiry(cellData.get(5).toString());


                if (!(NumberUtils.isDigits(cellData.get(4).toString())) && !(cellData.get(4).toString().contains("ERROR"))) {
                    creditRequest.setSortCode("ERROR: Not a Number");
                } else if (cellData.get(4).toString().contains("ERROR")) {
                    creditRequest.setSortCode("ERROR: Not a Number");
                } else {
                    String bankCode = cellData.get(4).toString();
                    FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByBankCode(bankCode);
                    if (financialInstitution != null) {
                        String sortCode = financialInstitution.getSortCode();
                        creditRequest.setSortCode(sortCode);
                        creditRequest.setBeneficiaryBank(financialInstitution.getInstitutionName());
                    } else {
                        creditRequest.setSortCode("ERROR: Invalid Bank Code");
                    }
                }
                crLists.add(creditRequest);

            }


        } catch (IllegalArgumentException e) {
            logger.error("Error uploading file", e);
        }
        DataTablesOutput<CreditRequestDTO> dto = new DataTablesOutput<>();
        dto.setData(crLists);
        dto.setRecordsFiltered(crLists.size());
        dto.setRecordsTotal(crLists.size());

//        if (session.getAttribute("inputstream") != null) {
//            session.removeAttribute("inputstream");
//        }

        return dto;


    }


    @PostMapping("/save")
    public String saveTransfer(WebRequest request, HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale, Principal principal) {

        try {

            if (principal == null || principal.getName() == null) {
                return "redirect:/login/corporate";
            }

            transferUtils.validateTransferCriteria();

            String tokenCode = request.getParameter("token");
            List<String> accountList = new ArrayList<>();
            accountList = (List<String>) session.getAttribute("accountList");
            CorporateUser user = corporateUserService.getUserByName(principal.getName());
            Corporate corporate = user.getCorporate();

            SettingDTO setting = configService.getSettingByName("ENABLE_CORPORATE_2FA");

            if (setting != null && setting.isEnabled()) {

                if (tokenCode != null && !tokenCode.isEmpty()) {

                    try {
                        boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), tokenCode);
                        if (!result) {
                            model.addAttribute("accounts", accountList);
                            model.addAttribute("failure", messageSource.getMessage("token.auth.failure", null, locale));
                            return "corp/transfer/bulktransfer/add";
                        }
                    } catch (InternetBankingSecurityException ibe) {
                        logger.error("Error authenticating token {} ", ibe);
                        model.addAttribute("accounts", accountList);
                        model.addAttribute("failure", messageSource.getMessage("token.auth.failure", null, locale));
                        return "corp/transfer/bulktransfer/add";
                    }
                } else {
                    model.addAttribute("accounts", accountList);
                    model.addAttribute("failure", "Token code is required");
                    return "corp/transfer/bulktransfer/add";
                }
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();


            String requests = request.getParameter("requests");
            String debitAccount = request.getParameter("debitAccount");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            List<CreditRequest> requestList = mapper.readValue(requests, new TypeReference<>() {
            });
            BigDecimal totalTransferAmount = new BigDecimal("0.00");
            BulkTransfer bulkTransfer = new BulkTransfer();
            bulkTransfer.setCustomerAccountNumber(debitAccount);
            bulkTransfer.setCorporate(corporate);
            bulkTransfer.setTranDate(date);

            requestList.forEach(i -> {
                String refNumber;
                do {
                    refNumber = transferUtils.generateReferenceNumber(12);
                }
                while (bulkTransferService.creditRequestRefNumberExists(refNumber));
                i.setReferenceNumber(refNumber);
            });
            bulkTransfer.setCrRequestList(requestList);

            String refCode;
            do {
                refCode = transferUtils.generateReferenceNumber(10);
            }
            while (bulkTransferService.refCodeExists(refCode));
            bulkTransfer.setReferenceNumber(refCode);
            bulkTransfer.setRefCode(refCode);

            for (CreditRequest creditRequest : requestList) {
//                BigDecimal crAmount = new BigDecimal(creditRequest.getAmount());
                totalTransferAmount = totalTransferAmount.add(creditRequest.getAmount());
                creditRequest.setBulkTransfer(bulkTransfer);
                creditRequest.setStatus("PENDING");
            }
            bulkTransferService.transactionAboveLimit(totalTransferAmount, debitAccount);
            bulkTransfer.setAmount(totalTransferAmount);
            bulkTransfer.setTransferType(TransferType.NAPS);
            String message = "";
            if (corporate.getCorporateType().equalsIgnoreCase("MULTI")) {
                message = bulkTransferService.saveBulkTransferRequestForAuthorization(bulkTransfer);
            } else if (corporate.getCorporateType().equalsIgnoreCase("SOLE")) {
                message = bulkTransferService.makeBulkTransferRequest(bulkTransfer);
            } else {
                return "redirect:/login/corporate";
            }
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/corporate/transfer/bulk";
        } catch (TransferRuleException t) {
            redirectAttributes.addFlashAttribute("failure", t.getMessage());
            return "redirect:/corporate/transfer/bulk";
        } catch (InternetBankingTransferException e) {
            String errorMessage = transferErrorService.getMessage(e);
            redirectAttributes.addFlashAttribute("failure", errorMessage);
            return "redirect:/corporate/transfer/bulk";

        } catch (InternetBankingException ibe) {
            logger.error("Error creating transfer", ibe);
            redirectAttributes.addFlashAttribute("failure", ibe.getMessage());
            return "redirect:/corporate/transfer/bulk";
        } catch (Exception e) {
            logger.error("Error creating transfer", e);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("bulk.save.success", null, null));
            return "redirect:/corporate/transfer/bulk";
        }
    }


    @GetMapping(path = "/alltransfers")
    public
    @ResponseBody
    DataTablesOutput<BulkTransferDTO> getAllTransfers(DataTablesInput input, Principal principal) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<BulkTransferDTO> transfers = bulkTransferService.getBulkTransferRequests(pageable);
        DataTablesOutput<BulkTransferDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(transfers.getContent());
        out.setRecordsFiltered(transfers.getTotalElements());
        out.setRecordsTotal(transfers.getTotalElements());
        return out;
    }


    @GetMapping(path = "/{bulkTransfer}/allcreditrequests")
    public
    @ResponseBody
    DataTablesOutput<CreditRequestDTO> getAllTransfers(DataTablesInput input, @PathVariable BulkTransfer bulkTransfer) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CreditRequestDTO> creditRequests = bulkTransferService.getCreditRequests(bulkTransfer, pageable);
        DataTablesOutput<CreditRequestDTO> output = new DataTablesOutput<>();
        output.setDraw(input.getDraw());
        output.setData(creditRequests.getContent());
        output.setRecordsFiltered(creditRequests.getTotalElements());
        output.setRecordsTotal(creditRequests.getTotalElements());
        return output;
    }

    @GetMapping(path = "/{bulkTransfer}/status")
    public
    @ResponseBody
    List<BulkStatusDTO> getStatus(@PathVariable BulkTransfer bulkTransfer) {

        List<BulkStatusDTO> bulkStatus = bulkTransferService.getStatus(bulkTransfer);
        System.out.println(bulkStatus + "bulkStatus");
        return bulkStatus;
    }


    @GetMapping("/bulk/receipt/{id}")
    public void getBulkTransferReceipt(@PathVariable Long id, Model model, Principal principal, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, Locale locale) throws Exception {

        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        Corporate corporate = corporateUser.getCorporate();
        CreditRequestDTO creditRequest = bulkTransferService.getCreditRequest(id);


        Map<String, Object> modelMap = new HashMap<>();
        double amount = Double.parseDouble(creditRequest.getAmount().toString());
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        modelMap.put("datasource", new ArrayList<>());
        modelMap.put("imagePath", imagePath);
        modelMap.put("amount", formatter.format(amount));
        modelMap.put("customer", corporate.getName());
        modelMap.put("customerAcctNumber", StringUtil.maskAccountNumber(creditRequest.getCustomerAccountNumber()));
        if (creditRequest.getNarration() != null) {
            modelMap.put("remarks", creditRequest.getNarration());
        } else {
            modelMap.put("remarks", "");
        }
        modelMap.put("beneficiary", creditRequest.getAccountName());
        modelMap.put("beneficiaryAcctNumber", creditRequest.getAccountNumber());
        modelMap.put("beneficiaryBank", creditRequest.getBeneficiaryBank());
        modelMap.put("refNUm", creditRequest.getReferenceNumber());
        modelMap.put("tranDate", DateFormatter.format(creditRequest.getTranDate()));
        modelMap.put("status", creditRequest.getStatus());
        modelMap.put("date", DateFormatter.format(new Date()));

       JasperReport jasperReport = ReportHelper.getJasperReport("credit_request_receipt");

        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment; filename=\"reciept.pdf\"");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap);
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }


    @GetMapping("/bulk/{id}/pdf")
    public void downloadBulkTransferPDFReport(@PathVariable Long id, ModelMap modelMap, Principal principal, HttpServletResponse response, Locale locale) throws Exception {

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");


        BulkTransfer bulkTransferRequest = bulkTransferService.getBulkTransferRequest(id);
        List<CreditRequestDTO> creditRequests = bulkTransferService.getCreditRequests(id);


        modelMap.put("datasource", creditRequests);
        modelMap.put("format", "pdf");

        Date today = new Date();
        modelMap.put("today", today);
        modelMap.put("imagePath", imagePath);
        modelMap.put("customerAccountNumber", bulkTransferRequest.getCustomerAccountNumber());
        Account account = accountService.getAccountByAccountNumber(bulkTransferRequest.getCustomerAccountNumber());
        modelMap.put("customerAccountName", account.getAccountName());


        JasperReport jasperReport = ReportHelper.getJasperReport("rpt_bulk_transfer");

        response.setContentType("application/x-download");
        response.setHeader("Content-Disposition", "attachment; filename=\"bulk_transfer.pdf\"");

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, modelMap, new JRBeanCollectionDataSource(creditRequests));
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());

    }

    @GetMapping("/bulk/{id}/excel")
    public void downloadBulkTransferExcelReport(@PathVariable Long id, ModelMap modelMap, Principal principal, RedirectAttributes redirectAttributes, Locale locale, HttpServletResponse response) throws Exception {


        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");


        BulkTransfer bulkTransferRequest = bulkTransferService.getBulkTransferRequest(id);
        List<CreditRequestDTO> creditRequests = bulkTransferService.getCreditRequests(id);

        logger.debug("Credit requests: {}", creditRequests);

        modelMap.put("datasource", creditRequests);
        modelMap.put("format", "pdf");

        Date today = new Date();
        modelMap.put("today", today);
        modelMap.put("imagePath", imagePath);
        modelMap.put("customerAccountNumber", bulkTransferRequest.getCustomerAccountNumber());
        Account account = accountService.getAccountByAccountNumber(bulkTransferRequest.getCustomerAccountNumber());
        modelMap.put("customerAccountName", account.getAccountName());

        File file = new File(jrxmlBulkExcelFile);

        JRDataSource dataSource = new JRBeanCollectionDataSource(creditRequests);
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(file);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, modelMap, dataSource);
        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        ByteArrayOutputStream pdfReportStream = new ByteArrayOutputStream();
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(pdfReportStream));

        exporter.exportReport();
        response.setHeader("Content-Length", String.valueOf(pdfReportStream.size()));
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "inline; filename=\"" + "Bulk_transfer_report.xlsx" + "\"");
        OutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(pdfReportStream.toByteArray());

        responseOutputStream.close();
        pdfReportStream.close();
        responseOutputStream.flush();

    }


    @GetMapping("/refresh/naps")
    @ResponseBody
    public void refreshNapsStatus(Principal principal) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        CorporateUser corporateUser = corporateUserService.getUserByName(principal.getName());

        Corporate corporate = corporateUser.getCorporate();

        List<BulkTransfer> bulkTransferRequest = bulkTransferService.getBulkTransferRequestsForCorporate(corporate);

        transferStatusJobLauncher.updateTransferStatusJob(bulkTransferRequest);
        logger.info("NAPS refresh update done");

    }

    @Scheduled(cron = "${naps.status.check.rate}")
    public void startUpdateTransferStatusJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        List<BulkTransfer> transferList = bulkTransferService.getByStatus();
        logger.info("transList {}", transferList);
        transferStatusJobLauncher.updateTransferStatusJob(transferList);
        logger.info("NAPS cron update done");
    }


}
