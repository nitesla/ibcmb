package longbridge.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import longbridge.models.AuditConfig;
import longbridge.services.AuditConfigService;


/**
 * Created by ayoade_farooq@yahoo.com on 4/18/2017.
 */
@Controller
@RequestMapping(value = "/admin/audit")
public class AdmAuditController {

    @Autowired
    AuditConfigService auditCfgService;

    @GetMapping()
    public String listAudit(Model model){
    	
    	Iterable<AuditConfig> entities = auditCfgService.getAllEntities();
        model.addAttribute("tables",entities);
        return "adm/setting/audit";
    }
    
    
    
    @PostMapping
    @ResponseBody
    public String changeAuditEntry(@RequestBody AuditConfig auditEntry) {
       auditCfgService.saveAuditConfig(auditEntry);
       return "success";
    }
}
