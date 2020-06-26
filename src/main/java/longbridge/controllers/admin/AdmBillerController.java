package longbridge.controllers.admin;

import longbridge.models.Biller;
import longbridge.models.PaymentItem;
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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    public String indexPage(){
        return "adm/quickteller/quicktellerpayments";
    }


    @PostMapping
    @ResponseBody
    public String updateBillers(){
        billerService.updateBillers();
        return "Successfully updated";
    }



    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<Biller> getAllCategoriesAndDescription(DataTablesInput input, @RequestParam("csearch") String search){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<Biller> biller = null;
        if (StringUtils.isNoneBlank(search)) {
            logger.info("GOT TO THE SEARCH SERVICE {}",search);
            biller = billerService.findEntities(search,pageable);
            logger.info("biller details {}", biller.getNumberOfElements());
        }else{
            biller = billerService.getEntities(pageable);
        }
        DataTablesOutput<Biller> out = new DataTablesOutput<Biller>();
        out.setDraw(input.getDraw());
        out.setData(biller.getContent());
        out.setRecordsFiltered(biller.getTotalElements());
        out.setRecordsTotal(biller.getTotalElements());
        return out;
    }



    @ResponseBody
    @GetMapping("/paymentitems")
    public List<PaymentItem> paymentItemTable(HttpServletRequest request){
        List<PaymentItem> paymentItems = (List<PaymentItem>) request.getSession().getAttribute("billeritems");
        return paymentItems;
    }




    @GetMapping("/{id}/edit")
    public String editBillerCategory(@PathVariable Long id, Model model, HttpServletRequest request) {
        Biller getBillerDetails = billerService.getBiller(id);
        List<PaymentItem> paymentItems = billerService.getPaymentItemForBiller(getBillerDetails.getBillerId(),id);
        request.getSession().setAttribute("billeritems",paymentItems);
        model.addAttribute("biller",getBillerDetails);
        return "adm/quickteller/edit";
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
