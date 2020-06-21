package longbridge.controllers.admin;

import longbridge.billerresponse.Biller;
import longbridge.dtos.BillerDTO;
import longbridge.dtos.FinancialInstitutionDTO;
import longbridge.models.Billers;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/payments")
public class AdmPaymentsController {

    @Autowired
    private BillerService billerService;

    @Autowired
    private BillerRepo billerRepo;

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
    List<Billers> getAllBillers() {
        return billerRepo.findAll();
    }



}
