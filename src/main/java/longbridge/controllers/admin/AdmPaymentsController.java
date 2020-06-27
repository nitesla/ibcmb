package longbridge.controllers.admin;

import longbridge.models.Biller;
import longbridge.repositories.BillersRepo;
import longbridge.services.BillerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/admin/payments")
public class AdmPaymentsController {

    @Autowired
    private BillerService billerService;

    @Autowired
    private BillersRepo billerRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/billers")
    public String indexPage(){
        return "quicktellerpayments";
    }


    @PostMapping("/updateBillers")
    @ResponseBody
    public String updateBillers(){
        return billerService.updateBillersTable();
    }


    @GetMapping(path = "/all")
    public
    @ResponseBody
    List<Biller> getAllBillers() {
        return billerRepo.findAll();
    }



}
