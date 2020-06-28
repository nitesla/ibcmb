package longbridge.controllers.admin;

import longbridge.dtos.BillerCategoryDTO;
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
        billerService.updateBillers();
        billerService.refreshCategories();
        return "Successfully updated";
    }




    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<BillerCategory> getAllCategoriesAndDescription(DataTablesInput input, @RequestParam("csearch") String search){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<BillerCategory>  billerCategory = billerService.getBillerCategories(pageable);

        DataTablesOutput<BillerCategory> out = new DataTablesOutput<BillerCategory>();
        out.setDraw(input.getDraw());
        out.setData(billerCategory.getContent());
        out.setRecordsFiltered(billerCategory.getTotalElements());
        out.setRecordsTotal(billerCategory.getTotalElements());
        return out;
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
        return "adm/quickteller/edit";
    }

    @ResponseBody
    @PostMapping("/updatepaymentitems")
    public String updatePaymentItems(HttpServletRequest request){
        logger.info("DEBUGGING1");
        Long billerId = (Long) request.getSession().getAttribute("billerId");
        billerService.updatePaymentItems(billerId);
        return "Successfully Updated";
    }


    @GetMapping("/{id}/edit")
    public String editBillerCategory(@PathVariable Long id, Model model, HttpServletRequest request) {
        BillerCategory getCategoryId = billerCategoryRepo.findOneById(id);
       String categoryName = getCategoryId.getCategoryName();
       request.getSession().setAttribute("categoryname", categoryName);
//        Biller getBillerDetails = billerService.getBiller(id);
//        model.addAttribute("biller",getBillerDetails);
        return "adm/quickteller/billers";
    }

    @ResponseBody
    @PostMapping("/disablebiller")
    public void disable(Long id){
        logger.info("disabling biller");
    billerService.disableBiller(id);
    }

    @ResponseBody
    @PostMapping("/enablebiller")
    public void enable(Long id){
        logger.info("enabling biller");
        billerService.enableBiller(id);
    }

    @ResponseBody
    @PostMapping("/authorizepaymentitem")
    public void enableOrDisablePaymentItem(Long id,Boolean value){
        logger.info("value = {}",value);
        logger.info("id = " + id);
        billerService.enablePaymentItems(id,value);
    }



}
