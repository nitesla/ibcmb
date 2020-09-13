package longbridge.controllers.admin;

import longbridge.models.BillerCategory;
import longbridge.models.NeftTransfer;
import longbridge.repositories.NeftTransferRepo;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/neft")
public class AdmNeftController {

    @Autowired
    private NeftTransferRepo neftTransferRepo;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping
    public String indexPage(){
        return "adm/nefttransfer/pageiA";
    }


    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<NeftTransfer> getAllCategoriesAndDescription(DataTablesInput input, @RequestParam("csearch") String search){
        logger.info("SEARCH = " + search);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<NeftTransfer> neftRequests = null;
            neftRequests = neftTransferRepo.findAll(pageable);
        DataTablesOutput<NeftTransfer> out = new DataTablesOutput<NeftTransfer>();
        out.setDraw(input.getDraw());
        out.setData(neftRequests.getContent());
        out.setRecordsFiltered(neftRequests.getTotalElements());
        out.setRecordsTotal(neftRequests.getTotalElements());
        return out;
    }


}
