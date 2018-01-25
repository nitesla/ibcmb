package longbridge.controllers.corporate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import longbridge.dtos.*;
import longbridge.exception.*;
import longbridge.models.*;
import longbridge.services.*;
import longbridge.utils.TransferUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.StreamSupport;

/**
 * Created by Fortune on 5/30/2017.
 */

@Controller
@RequestMapping("/corporate/transfer")
public class CorpNAPSTransferController {

    //private static final String SERVER_FILE_PATH = "C:\\ibanking\\files\\Copy-of-NEFT-ECOB-ABC-old-mutual.xls";
    @Value("${napsfile.path}")
    private String SERVER_FILE_PATH;
    private static final String FILENAME = "Copy-of-NEFT-ECOB-ABC-old-mutual.xls";
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MessageSource messageSource;
    private AccountService accountService;
    private CorporateUserService corporateUserService;
    private CorpTransferService corpTransferService;
    private CorporateService corporateService;
    private BulkTransferService bulkTransferService;
    private SecurityService securityService;
    private FinancialInstitutionService financialInstitutionService;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private TransferErrorService transferErrorService;
    @Autowired
    private TransferUtils transferUtils;

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
        this.corpTransferService = corpTransferService;
        this.corporateService = corporateService;
    }

    @GetMapping("/bulk")
    public String getBulkTransfers(Model model, HttpSession session) {
        if (session.getAttribute("workbook") != null) {
            session.removeAttribute("workbook");
        }
        return "/corp/transfer/bulktransfer/list";
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
            logger.info("Roles not In Auth List..{}", rolesNotInAuthList.toString());
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
        return "/bankcodes";
    }

    @GetMapping(path = "/allbankcodes")
    public
    @ResponseBody
    DataTablesOutput<FinancialInstitutionDTO> getAllFis(DataTablesInput input) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<FinancialInstitutionDTO> fis = financialInstitutionService.getFinancialInstitutionsWithSortCode(pageable);
        DataTablesOutput<FinancialInstitutionDTO> out = new DataTablesOutput<FinancialInstitutionDTO>();
        out.setDraw(input.getDraw());
        out.setData(fis.getContent());
        out.setRecordsFiltered(fis.getTotalElements());
        out.setRecordsTotal(fis.getTotalElements());
        return out;
    }


    @GetMapping("/bulk/download")
    public String downloadFile(HttpServletResponse response, Model model, Locale locale) throws IOException {
        File file = null;
        file = new File(SERVER_FILE_PATH);
        if (!file.exists()) {
            model.addAttribute("failure", messageSource.getMessage("file.not.found", null, locale));
            return "/corp/transfer/bulktransfer/upload";
        }
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
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
            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
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
    public DataTablesOutput<CreditRequestDTO> getCreditRequests(HttpSession session, Model model) throws IOException {
        Workbook workbook = (Workbook) session.getAttribute("workbook");
        String fileExtension = (String) session.getAttribute("fileExtension");
        List<CreditRequestDTO> crLists = new ArrayList<>();
        try {
            System.out.println(workbook);

            Sheet datatypeSheet = workbook.getSheetAt(0);

            Iterator<Row> iterator = datatypeSheet.iterator();

            if (iterator.hasNext()) iterator.next();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                //Iterator<Cell> cellIter ator = currentRow.iterator();
                ArrayList cellData = new ArrayList();
                for (int i = 0; i < 5; i++) {
                    Cell currentCell = currentRow.getCell(i);
                    System.out.println(currentCell);
                    if (currentCell == null || currentCell.getCellType() == Cell.CELL_TYPE_BLANK || currentCell.toString().isEmpty() || currentCell.toString() == null) {
                        cellData.add("ERROR: Empty cell");
                    } else {
                        cellData.add(currentCell);
                    }


                }

                int rowIndex = currentRow.getRowNum();
                CreditRequestDTO creditRequest = new CreditRequestDTO();
                Long id = Long.valueOf(rowIndex);
                creditRequest.setId(id);

                if (!(NumberUtils.isDigits(cellData.get(0).toString())) && !(cellData.get(0).toString().contains("ERROR"))) {
                    creditRequest.setAccountNumber("ERROR: Not a number");
                } else {
                    creditRequest.setAccountNumber(cellData.get(0).toString());
                }

                creditRequest.setAccountName(cellData.get(1).toString());

                if (!(NumberUtils.isNumber(cellData.get(2).toString())) && !(cellData.get(2).toString().contains("ERROR"))) {
                    creditRequest.setAmount("ERROR: Invalid Amount");
                } else {
                    creditRequest.setAmount(cellData.get(2).toString());
                }

                creditRequest.setNarration(cellData.get(3).toString());


                if (!(NumberUtils.isDigits(cellData.get(4).toString())) && !(cellData.get(4).toString().contains("ERROR"))) {
                    creditRequest.setSortCode("ERROR: Not a Number");
                } else if (cellData.get(4).toString().contains("ERROR")) {
                    creditRequest.setSortCode("ERROR: Not a Number");
                } else {
                    String bankCode = cellData.get(4).toString();
                    FinancialInstitution financialInstitution = financialInstitutionService.getFinancialInstitutionByBankCode(bankCode);
                    if(financialInstitution != null) {
                        String sortCode = financialInstitution.getSortCode();
                        creditRequest.setSortCode(sortCode);
                    }else{
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
            List<CreditRequest> requestList = mapper.readValue(requests, new TypeReference<List<CreditRequest>>() {
            });
            BigDecimal totalTransferAmount = new BigDecimal("0.00");
            BulkTransfer bulkTransfer = new BulkTransfer();
            bulkTransfer.setCustomerAccountNumber(debitAccount);
            bulkTransfer.setCrRequestList(requestList);
            bulkTransfer.setCorporate(corporate);
            bulkTransfer.setTranDate(date);

            while (!bulkTransferService.refCodeExists(generateRefCode())) {
                bulkTransfer.setRefCode(generateRefCode());
                break;
            }

            for (CreditRequest creditRequest : requestList) {
                BigDecimal crAmount = new BigDecimal(creditRequest.getAmount());
                totalTransferAmount = totalTransferAmount.add(crAmount);
                creditRequest.setBulkTransfer(bulkTransfer);
                creditRequest.setStatus("P");
            }
            bulkTransfer.setAmount(totalTransferAmount);
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

    private String generateRefCode() {
        Random r = new Random(System.currentTimeMillis());
        int random = 100000 + r.nextInt(999999);
        String refCode = Integer.toString(random);
        return refCode;
    }

    @GetMapping(path = "/alltransfers")
    public
    @ResponseBody
    DataTablesOutput<BulkTransferDTO> getAllTransfers(DataTablesInput input, Principal principal) {

        CorporateUser user = corporateUserService.getUserByName(principal.getName());
        Corporate corporate = user.getCorporate();
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<BulkTransferDTO> transfers = bulkTransferService.getBulkTransferRequests(corporate, pageable);
        DataTablesOutput<BulkTransferDTO> out = new DataTablesOutput<BulkTransferDTO>();
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
        System.out.println(bulkStatus);
        return bulkStatus;
    }


}
