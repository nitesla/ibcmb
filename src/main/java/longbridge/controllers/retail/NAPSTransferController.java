package longbridge.controllers.retail;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.api.NEnquiryDetails;
import longbridge.dtos.BulkTransferDTO;
import longbridge.dtos.CreditRequestDTO;
import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.dtos.SettingDTO;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.models.*;
import longbridge.services.*;
import longbridge.services.bulkTransfers.TransferStatusJobLauncher;
import longbridge.utils.DataTablesUtils;
import longbridge.utils.TransferType;
import longbridge.utils.TransferUtils;
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
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
@RequestMapping("/retail/transfer")
public class NAPSTransferController {

    @Autowired
    private MessageSource messageSource;

    private AccountService accountService;
    private RetailUserService retailUserService;
    private BulkRetailTransferService bulkRetailTransferService;
    private FinancialInstitutionService financialInstitutionService;
    @Autowired
    IntegrationService integrationService;
    @Autowired
    private TransferStatusJobLauncher transferStatusJobLauncher;
    @Autowired
    private TransferUtils transferUtils;
    @Autowired
    private ConfigurationService configService;
    @Autowired
    private SecurityService securityService;




    @Autowired
    public NAPSTransferController(AccountService accountService, RetailUserService retailUserService, BulkRetailTransferService bulkRetailTransferService,FinancialInstitutionService financialInstitutionService) {
        this.accountService = accountService;
        this.retailUserService = retailUserService;
        this.bulkRetailTransferService = bulkRetailTransferService;
        this.financialInstitutionService=financialInstitutionService;
    }

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String SERVER_FILE_PATH = "/Users/user/Documents/UPLOAD_FOLDER/Copy-of-NEFT-ECOB-ABC-old-mutual.xls";


    @GetMapping("/bulk")
    public String getBulkTransfers(Model model) {
        return "/cust/transfer/bulktransfer/list";
    }

    @GetMapping("/{id}/view")
    public String getBulkTransferCreditRequests(@PathVariable Long id, Model model) {
        BulkTransfer bulkTransfer = bulkRetailTransferService.getBulkTransferRequest(id);
        model.addAttribute("bulkTransfer",bulkTransfer);
        return "/cust/transfer/bulktransfer/crlistview";
    }

    @GetMapping("/upload")
    public String getUploadBulkTransferFile(Model model) {

        return "/cust/transfer/bulktransfer/upload";
    }

    @GetMapping("/add")
    public String addBulkTransfer(Model model) {
        return "/cust/transfer/bulktransfer/add";
    }
    /*
        * Download a file from
        *   - inside project, located in resources folder.
        *   Added by Bimpe Ayoola
     */
    //private static final String SERVER_FILE_PATH="C:\\ibanking\\files\\Copy-of-NEFT-ECOB-ABC-old-mutual.xls\\";
    private static final String FILENAME = "bulk_transfer_upload_file.xls";

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
    public void downloadFile(HttpServletResponse response) throws IOException {
        File file = null;
        //file = new File(SERVER_FILE_PATH);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        file = new File(classloader.getResource(FILENAME).getFile());
        if (!file.exists()) {
            String errorMessage = "Sorry. The file you are looking for does not exist";
            System.out.println(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return;
        }
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            System.out.println("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }
        System.out.println("mimetype : " + mimeType);
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
        response.setContentLength((int) file.length());
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        //Copy bytes from source to destination(outputstream in this example), closes both streams.
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

 /* End of the addition for file download by Bimpe Ayoola*/


    @PostMapping("/bulk/upload")
    public String uploadBulkTransferFile(@RequestParam("transferFile") MultipartFile file, Principal principal, RedirectAttributes redirectAttributes, Model model, Locale locale, HttpSession httpSession) throws IOException {

        if (principal == null || principal.getName() == null) {
            return "redirect:/login/corporate";
        }
        RetailUser user = retailUserService.getUserByName(principal.getName());
        List<String> accountList = new ArrayList<>();
        if (user != null) {

            Iterable<Account> accountNumbers = accountService.getAccountsForDebit(user.getCustomerId());

            StreamSupport.stream(accountNumbers.spliterator(), false)
                    .filter(Objects::nonNull)
                    .forEach(i -> accountList.add(i.getAccountNumber()));
            model.addAttribute("accounts", accountList);
        }

        if (file.isEmpty()) {
            model.addAttribute("failure", messageSource.getMessage("file.require", null, locale));
            return "/cust/transfer/bulktransfer/upload";
        }


        // Get the file, perform some validations and save it in a session
        try {
            byte[] bytes = file.getBytes();
            logger.info("here one");
            InputStream inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();
            logger.info("here two {}", filename);
            String extension = filename.substring(filename.lastIndexOf(".") + 1);
            logger.info("here three {}", extension);
            // File type validation
            Workbook workbook;
            if (extension.equalsIgnoreCase("xls")) {

                workbook = new HSSFWorkbook(inputStream);
                logger.info("here two four");

            } else if (extension.equalsIgnoreCase("xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
                logger.info("here two five");
            } else {

                model.addAttribute("failure", messageSource.getMessage("file.format.failure", null, locale));
                return "/cust/transfer/bulktransfer/upload";
            }

            // File content validation using number of header cells
            Sheet datatypeSheet = workbook.getSheetAt(0);
            logger.info("here two six", datatypeSheet);
            Iterator<Row> iterator = datatypeSheet.iterator();
            if (iterator.hasNext()) {
                Row headerRow = iterator.next();
                if (headerRow.getLastCellNum() > 5) {
                    model.addAttribute("failure", messageSource.getMessage("file.content.failure", null, locale));
                    return "/cust/transfer/bulktransfer/upload";
                }
            }

            httpSession.setAttribute("accountList", accountList);
            logger.info("here two seven");
            httpSession.setAttribute("workbook", workbook);
            httpSession.setAttribute("fileExtension", extension);
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("file.upload.success", null, locale));
        } catch (IOException e) {
            logger.error("Error uploading file", e);

            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("file.upload.failure", null, locale));
            return "redirect:/retail/transfer/upload";
        }

        return "/cust/transfer/bulktransfer/add";
    }



    @GetMapping("/all")
    @ResponseBody
    public DataTablesOutput<CreditRequestDTO> getCreditRequests(HttpSession session, Model model,Locale locale) throws IOException {
        Workbook workbook = (Workbook) session.getAttribute("workbook");
        String fileExtension = (String) session.getAttribute("fileExtension");
        model.addAttribute("accountList", session.getAttribute("accountList"));

        List<CreditRequestDTO> crLists = new ArrayList<>();
        try {
            System.out.println(workbook);

            Sheet datatypeSheet = workbook.getSheetAt(0);

            for(Row row:datatypeSheet){
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
                Long id = Long.valueOf(rowIndex);
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
                    creditRequest.setAmount(new BigDecimal( cellData.get(2).toString()));
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
    public String saveTransfer(WebRequest request,HttpSession session, RedirectAttributes redirectAttributes, Model model, Locale locale, Principal principal, HttpServletRequest httpServletRequest){

        try {

            if (principal == null || principal.getName() == null) {
                return "redirect:/login/retail";
            }

            transferUtils.validateTransferCriteria();

            String tokenCode = request.getParameter("token");
            List<String> accountList ;
            accountList = (List<String>) session.getAttribute("accountList");
            RetailUser user=retailUserService.getUserByName(principal.getName());

            SettingDTO setting = configService.getSettingByName("ENABLE_RETAIL_2FA");

            if (setting != null && setting.isEnabled()) {

                if (tokenCode != null && !tokenCode.isEmpty()) {

                    try {
                        boolean result = securityService.performTokenValidation(user.getEntrustId(), user.getEntrustGroup(), tokenCode);
                        if (!result) {
                            model.addAttribute("accounts", accountList);
                            model.addAttribute("failure", messageSource.getMessage("token.auth.failure", null, locale));
                            return "cust/transfer/bulktransfer/add";
                        }
                    } catch (InternetBankingSecurityException ibe) {
                        logger.error("Error authenticating token {} ", ibe);
                        model.addAttribute("accounts", accountList);
                        model.addAttribute("failure", messageSource.getMessage("token.auth.failure", null, locale));
                        return "cust/transfer/bulktransfer/add";
                    }
                } else {
                    model.addAttribute("accounts", accountList);
                    model.addAttribute("failure", "Token code is required");
                    return "cust/transfer/bulktransfer/add";
                }
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            String requests = request.getParameter("requests");
            System.out.println(requests);
            String debitAccount = request.getParameter("debitAccount");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            List<CreditRequest> requestList = mapper.readValue(requests, new TypeReference<List<CreditRequest>>() {
            });

            logger.info("Transfer List {}",requestList);

            BigDecimal totalTransferAmount = new BigDecimal("0.00");
            BulkTransfer bulkTransfer = new BulkTransfer();
            bulkTransfer.setCustomerAccountNumber(debitAccount);
            bulkTransfer.setRetailUser(user);
            bulkTransfer.setTranDate(date);

            requestList.forEach(i -> {
                String refNumber;
                do{
                    refNumber = transferUtils.generateReferenceNumber(12);
                }
                while (bulkRetailTransferService.creditRequestRefNumberExists(refNumber));
                i.setReferenceNumber(refNumber);
            });
            bulkTransfer.setCrRequestList(requestList);

            String refCode;
            do {
                refCode = transferUtils.generateReferenceNumber(10);
            }
            while (bulkRetailTransferService.refCodeExists(refCode));
            bulkTransfer.setReferenceNumber(refCode);
            bulkTransfer.setRefCode(refCode);

            for(CreditRequest creditRequest : requestList){
                totalTransferAmount = totalTransferAmount.add(creditRequest.getAmount());
                creditRequest.setBulkTransfer(bulkTransfer);
                creditRequest.setStatus("S");
            }
            bulkRetailTransferService.transactionAboveLimit(totalTransferAmount,debitAccount);
            bulkTransfer.setAmount(totalTransferAmount);
            bulkTransfer.setTransferType(TransferType.NAPS);

            String message = bulkRetailTransferService.makeBulkTransferRequest(bulkTransfer);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/retail/transfer/bulk";
        } catch (Exception ibe) {
            logger.error("Error creating transfer", ibe);
            model.addAttribute("failure", messageSource.getMessage("bulk.transfer.failure", null, locale));
            return "/cust/transfer/bulktransfer/add";
        }
    }



//    @GetMapping(path = "/alltransfers")
//    public @ResponseBody DataTablesOutput<BulkTransferDTO> getAllTransfers(DataTablesInput input, Principal principal) {
//
//        CorporateUser user = corporateUserService.getUserByName(principal.getName());
//        Corporate corporate = user.getCorporate();
//        Pageable pageable = DataTablesUtils.getPageable(input);
//        Page<BulkTransferDTO> transfers = BulkRetailTransferService.getBulkTransferRequests(corporate, pageable);
//        DataTablesOutput<BulkTransferDTO> out = new DataTablesOutput<BulkTransferDTO>();
//        out.setDraw(input.getDraw());
//        out.setData(transfers.getContent());
//        out.setRecordsFiltered(transfers.getTotalElements());
//        out.setRecordsTotal(transfers.getTotalElements());
//        return out;
//    }
@GetMapping(path = "/alltransfers")
public
@ResponseBody
DataTablesOutput<BulkTransferDTO> getAllTransfers(DataTablesInput input, Principal principal) {

    Pageable pageable = DataTablesUtils.getPageable(input);
    Page<BulkTransferDTO> transfers = bulkRetailTransferService.getBulkTransferRequests(pageable);
    DataTablesOutput<BulkTransferDTO> out = new DataTablesOutput<BulkTransferDTO>();
    out.setDraw(input.getDraw());
    out.setData(transfers.getContent());
    out.setRecordsFiltered(transfers.getTotalElements());
    out.setRecordsTotal(transfers.getTotalElements());
    return out;
}

    @GetMapping(path = "/{bulkTransfer}/allcreditrequests")
    public @ResponseBody DataTablesOutput<CreditRequestDTO> getAllTransfers(DataTablesInput input,@PathVariable BulkTransfer bulkTransfer) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CreditRequestDTO> creditRequests = bulkRetailTransferService.getCreditRequests(bulkTransfer,pageable);
        DataTablesOutput<CreditRequestDTO> output = new DataTablesOutput<>();
        output.setDraw(input.getDraw());
        output.setData(creditRequests.getContent());
        output.setRecordsFiltered(creditRequests.getTotalElements());
        output.setRecordsTotal(creditRequests.getTotalElements());
        return output;
    }

    @GetMapping("/refresh/naps")
    @ResponseBody
    public void refreshNapsStatus(Principal principal) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        RetailUser user = retailUserService.getUserByName(principal.getName());


        List<BulkTransfer> bulkTransferRequest = bulkRetailTransferService.getBulkTransferRequestsForRetail(user);

        transferStatusJobLauncher.updateTransferStatusJob(bulkTransferRequest);
        logger.info("NAPS refresh update done");

    }

    @Scheduled(cron = "${naps.status.check.rate}")
    public void startUpdateTransferStatusJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        List<BulkTransfer> transferList = bulkRetailTransferService.getByStatus();
        logger.info("transList {}", transferList);
        transferStatusJobLauncher.updateTransferStatusJob(transferList);
        logger.info("NAPS cron update done");
    }



}
