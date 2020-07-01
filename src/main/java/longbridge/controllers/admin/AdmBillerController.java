package longbridge.controllers.admin;

import longbridge.models.Biller;
import longbridge.models.BillerCategory;
import longbridge.models.PaymentItem;
import longbridge.repositories.BillerCategoryRepo;
import longbridge.repositories.BillerRepo;
import longbridge.services.BillerService;
import longbridge.utils.DataTablesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/admin/billers")
public class AdmBillerController {

    @Autowired
    private BillerService billerService;

    @Autowired
    private BillerRepo billerRepo;

    @Autowired
    private BillerCategoryRepo billerCategoryRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    public String indexPage(){
        return "adm/quickteller/quicktellerpayments";
    }


    @PostMapping
    @ResponseBody
    public String updateBillers(){
        billerService.RefreshBiller();
        return "Successfully updated";
    }




    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<BillerCategory> getAllCategoriesAndDescription(DataTablesInput input, @RequestParam("csearch") String search){
        logger.info("SEARCH = " + search);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<BillerCategory> billerCategory = null;
        if (StringUtils.isNoneBlank(search)) {
            billerCategory = billerService.getBillerCategories(search,pageable);
        }else {
            billerCategory = billerService.getBillerCategories(pageable);
        }
        DataTablesOutput<BillerCategory> out = new DataTablesOutput<BillerCategory>();
        out.setDraw(input.getDraw());
        out.setData(billerCategory.getContent());
        out.setRecordsFiltered(billerCategory.getTotalElements());
        out.setRecordsTotal(billerCategory.getTotalElements());
        return out;
    }


    @ResponseBody
    @PostMapping("/assignreadonly")
    public void assignReadOnlyValue(Long id,Boolean value)
    {
        logger.info("value = {}", value);
        billerService.readOnlyAmount(id,value);
    }


    @ResponseBody
    @GetMapping("/paymentitems")
    public List<PaymentItem> paymentItemTable(HttpServletRequest request){
        List<PaymentItem> paymentItems = (List<PaymentItem>) request.getSession().getAttribute("billeritems");
        return paymentItems;
    }


    @PostMapping(path = "/categorybillers") public @ResponseBody
    DataTablesOutput<Biller> getAllBillersPerCategory(DataTablesInput input, @RequestParam("csearch") String search, HttpServletRequest request){
        logger.info("I JUST GOT HERE");
        Pageable pageable = DataTablesUtils.getPageable(input);
        String categoryname = (String) request.getSession().getAttribute("categoryname");
        Page<Biller>  billers = billerService.getBillersByCategory(categoryname,pageable);

        DataTablesOutput<Biller> out = new DataTablesOutput<Biller>();
        out.setDraw(input.getDraw());
        out.setData(billers.getContent());
        out.setRecordsFiltered(billers.getTotalElements());
        out.setRecordsTotal(billers.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/getpaymentitems")
    public String getPaymentItemForBiller(@PathVariable Long id, Model model,HttpServletRequest request){
        Biller biller = billerRepo.findOneById(id);
        Long billerId = biller.getBillerId();
        request.getSession().setAttribute("billerId" , billerId);
        List<PaymentItem> paymentItems = billerService.getPaymentItemsForBiller(id);
        request.getSession().setAttribute("billeritems",paymentItems);
        model.addAttribute("rowid",id);
        model.addAttribute("biller", biller);
        return "adm/quickteller/edit";
    }

    @ResponseBody
    @PostMapping("/updatepaymentitems")
    public String updatePaymentItems(HttpServletRequest request){
        Long billerId = (Long) request.getSession().getAttribute("billerId");
        billerService.refreshPaymentItems(billerId);
        return "Successfully Updated";
    }


    @GetMapping("/{id}/biller")
    public String editBillerCategory(@PathVariable Long id, Model model, HttpServletRequest request) {
        BillerCategory getCategoryId = billerCategoryRepo.findOneById(id);
        request.getSession().setAttribute("billerRowId", id);
       String categoryName = getCategoryId.getCategoryName();
       request.getSession().setAttribute("categoryname", categoryName);
       model.addAttribute("categoryname",getCategoryId);
        return "adm/quickteller/billers";
    }

    @GetMapping("/{id}/backtobillers")
    public String backButtonToBillerPage(@PathVariable Long id, Model model, HttpServletRequest request){
        Long billerTableId = (Long) request.getSession().getAttribute("billerRowId");
        logger.info("BILLER ID == " + billerTableId);
        BillerCategory getCategoryId = billerCategoryRepo.findOneById(billerTableId);
        String categoryName = getCategoryId.getCategoryName();
        request.getSession().setAttribute("categoryname", categoryName);
        model.addAttribute("categoryname",getCategoryId);
        return "adm/quickteller/billers";

    }

    @ResponseBody
    @PostMapping("/enableordisablecategory")
    public void disableOrEnableCategory(Long id, Boolean value){
    billerService.enableOrDisableCategory(id,value);
    }

    @ResponseBody
    @PostMapping("/enableordisablebiller")
    public void enableOrDisableBiller(Long id, Boolean value){
        billerService.enableOrDisableBiller(id,value);
    }

    @ResponseBody
    @PostMapping("/enableordisablepaymentitem")
    public void enableOrDisablePaymentItem(Long id,Boolean value){
        billerService.enablePaymentItems(id,value);
    }



}
