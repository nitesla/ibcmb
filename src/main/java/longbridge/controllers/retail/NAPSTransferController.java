package longbridge.controllers.retail;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.BulkTransferDTO;
import longbridge.dtos.CreditRequestDTO;
import longbridge.models.*;
import longbridge.services.AccountService;
import longbridge.services.BulkTransferService;
import longbridge.services.CorporateUserService;
import longbridge.services.RetailUserService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
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
    private BulkTransferService bulkTransferService;

    @Autowired
    public NAPSTransferController(AccountService accountService, RetailUserService retailUserService, BulkTransferService bulkTransferService) {
        this.accountService = accountService;
        this.retailUserService = retailUserService;
        this.bulkTransferService = bulkTransferService;
    }

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String SERVER_FILE_PATH = "/Users/user/Documents/UPLOAD_FOLDER/Copy-of-NEFT-ECOB-ABC-old-mutual.xls";


    @GetMapping("/bulk")
    public String getBulkTransfers(Model model) {
        return "/retail/transfer/bulktransfer/list";
    }

    @GetMapping("/{id}/view")
    public String getBulkTransferCreditRequests(@PathVariable Long id, Model model) {
        BulkTransfer bulkTransfer = bulkTransferService.getBulkTransferRequest(id);
        model.addAttribute("bulkTransfer",bulkTransfer);
        return "/retail/transfer/bulktransfer/crlistview";
    }

    @GetMapping("/upload")
    public String getUploadBulkTransferFile(Model model) {
        return "/retail/transfer/bulktransfer/upload";
    }

    @GetMapping("/add")
    public String addBulkTransfer(Model model) {
        return "/retail/transfer/bulktransfer/add";
    }
    /*
        * Download a file from
        *   - inside project, located in resources folder.
        *   Added by Bimpe Ayoola
     */
    //private static final String SERVER_FILE_PATH="C:\\ibanking\\files\\Copy-of-NEFT-ECOB-ABC-old-mutual.xls\\";
    private static final String FILENAME = "Copy-of-NEFT-ECOB-ABC-old-mutual.xls";

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
    public String uploadBulkTransfeFile(@RequestParam("transferFile") MultipartFile file, Principal principal, RedirectAttributes redirectAttributes, Model model, Locale locale, HttpSession httpSession) throws IOException {

        if (principal == null || principal.getName() == null) {
            return "redirect:/login/corporate";
        }
        RetailUser user = retailUserService.getUserByName(principal.getName());
        if (user != null) {

            List<String> accountList = new ArrayList<>();

            Iterable<Account> accountNumbers = accountService.getAccountsForDebit(user.getCustomerId());

            StreamSupport.stream(accountNumbers.spliterator(), false)
                    .filter(Objects::nonNull)
                    .forEach(i -> accountList.add(i.getAccountNumber()));
            System.out.println(accountList);
            model.addAttribute("accounts", accountList);
        }

        if (file.isEmpty()) {
            System.out.println("i got here");
            model.addAttribute("failure", messageSource.getMessage("file.required", null, locale));
            return "/retail/transfer/bulktransfer/add";
        }

        // Get the file and save it
        try {
            byte[] bytes = file.getBytes();
            InputStream inputStream = file.getInputStream();
            httpSession.setAttribute("inputStream" , inputStream);
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("file.upload.success", null, locale));

        } catch (IOException e) {
            logger.error("Error uploading file", e);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("file.upload.failure", null, locale));
        }

        return "/retail/transfer/bulktransfer/add";
    }


    @GetMapping("/all")
    @ResponseBody
    public DataTablesOutput<CreditRequestDTO> getCreditRequests(HttpSession session , Model model) throws IOException {
        Workbook workbook = (Workbook) session.getAttribute("workbook");
        String fileExtension = (String)session.getAttribute("fileExtension");
        List<CreditRequestDTO> crLists = new ArrayList<>();
        try {
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            if (iterator.hasNext()) iterator.next();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                ArrayList cellData = new ArrayList();
                for (int i = 0; i < currentRow.getLastCellNum(); i++){
                    Cell currentCell = currentRow.getCell(i);
                    System.out.println(currentCell);
                    if (currentCell == null || currentCell.getCellType() == Cell.CELL_TYPE_BLANK || currentCell.toString().isEmpty() || currentCell.toString() == null) {
                        cellData.add("ERROR HERE");
                    } else {
                        cellData.add(currentCell);
                    }
                }

                int rowIndex = currentRow.getRowNum();
                CreditRequestDTO creditRequest = new CreditRequestDTO();
                Long id = Long.valueOf(rowIndex);
                creditRequest.setId(id);
                creditRequest.setAccountNumber(cellData.get(0).toString());
                creditRequest.setAccountName(cellData.get(1).toString());
                creditRequest.setAmount(cellData.get(2).toString());
                creditRequest.setNarration(cellData.get(3).toString());
                crLists.add(creditRequest);

            }


        } catch (IllegalArgumentException e) {
            logger.error("Error uploading file", e);
        }
        System.out.println(crLists);
        DataTablesOutput<CreditRequestDTO> dto = new DataTablesOutput<>();
        dto.setData(crLists);
        dto.setRecordsFiltered(crLists.size());
        dto.setRecordsTotal(crLists.size());

        if (session.getAttribute("inputstream") != null) {
            session.removeAttribute("inputstream");
        }

        return dto;


    }


    @PostMapping("/save")
    public String saveTransfer(WebRequest request, RedirectAttributes redirectAttributes, Model model, Locale locale, Principal principal, HttpServletRequest httpServletRequest){

        try {

            if (principal == null || principal.getName() == null) {
                return "redirect:/login/corporate";
            }
//            CorporateUser user = corporateUserService.getUserByName(principal.getName());
//            Corporate corporate = user.getCorporate();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            String requests = request.getParameter("requests");
            System.out.println(requests);
            String debitAccount = request.getParameter("debitAccount");
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            List<CreditRequest> requestList = mapper.readValue(requests, new TypeReference<List<CreditRequest>>() {
            });

            System.out.println(requestList);

            BulkTransfer bulkTransfer = new BulkTransfer();
            bulkTransfer.setCustomerAccountNumber(debitAccount);
            bulkTransfer.setCrRequestList(requestList);
            //bulkTransfer.setCorporate(corporate);
            bulkTransfer.setTranDate(date);
            for(CreditRequest creditRequest : requestList){
                creditRequest.setBulkTransfer(bulkTransfer);
                creditRequest.setStatus("S");
            }
            String message = bulkTransferService.makeBulkTransferRequest(bulkTransfer);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/corporate/transfer/bulk";
        } catch (Exception ibe) {
            logger.error("Error creating transfer", ibe);
            model.addAttribute("failure", messageSource.getMessage("bulk.transfer.failure", null, locale));
            return "/corp/transfer/bulktransfer/add";
        }
    }



//    @GetMapping(path = "/alltransfers")
//    public @ResponseBody DataTablesOutput<BulkTransferDTO> getAllTransfers(DataTablesInput input, Principal principal) {
//
//        CorporateUser user = corporateUserService.getUserByName(principal.getName());
//        Corporate corporate = user.getCorporate();
//        Pageable pageable = DataTablesUtils.getPageable(input);
//        Page<BulkTransferDTO> transfers = bulkTransferService.getBulkTransferRequests(corporate, pageable);
//        DataTablesOutput<BulkTransferDTO> out = new DataTablesOutput<BulkTransferDTO>();
//        out.setDraw(input.getDraw());
//        out.setData(transfers.getContent());
//        out.setRecordsFiltered(transfers.getTotalElements());
//        out.setRecordsTotal(transfers.getTotalElements());
//        return out;
//    }


    @GetMapping(path = "/{bulkTransfer}/allcreditrequests")
    public @ResponseBody DataTablesOutput<CreditRequestDTO> getAllTransfers(DataTablesInput input,@PathVariable BulkTransfer bulkTransfer) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CreditRequestDTO> creditRequests = bulkTransferService.getCreditRequests(bulkTransfer,pageable);
        DataTablesOutput<CreditRequestDTO> output = new DataTablesOutput<>();
        output.setDraw(input.getDraw());
        output.setData(creditRequests.getContent());
        output.setRecordsFiltered(creditRequests.getTotalElements());
        output.setRecordsTotal(creditRequests.getTotalElements());
        return output;
    }



}