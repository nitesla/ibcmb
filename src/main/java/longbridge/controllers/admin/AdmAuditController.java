package longbridge.controllers.admin;

import jdk.internal.util.xml.impl.Input;
import longbridge.models.AdminUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
	public String listSettings(Model model) {
		return "adm/setting/audit";
	}
    
    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<AuditConfig> getAllCodes(DataTablesInput input,@RequestParam("csearch") String search)
    {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AuditConfig> auditConf = null;
        if (StringUtils.isNoneBlank(search)) {
        	auditConf = auditCfgService.findEntities(search,pageable);
		}else{
			auditConf = auditCfgService.getEntities(pageable);
		}
        
        DataTablesOutput<AuditConfig> out = new DataTablesOutput<AuditConfig>();
        out.setDraw(input.getDraw());
        out.setData(auditConf.getContent());
        out.setRecordsFiltered(auditConf.getTotalElements());
        out.setRecordsTotal(auditConf.getTotalElements());
        return out;
    }





    @GetMapping(path = "all/entityname")
    public @ResponseBody DataTablesOutput<AuditConfig> getAllEntities(DataTablesInput input)
    {
        Pageable pageable=DataTablesUtils.getPageable(input);
        Page<AuditConfig> auditConfig=null;
        auditConfig=auditCfgService.getEntities(pageable);
        DataTablesOutput<AuditConfig> out=new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(auditConfig.getContent());
        out.setRecordsFiltered(auditConfig.getTotalElements());
        out.setRecordsTotal(auditConfig.getTotalElements());
        return out;
    }


    @GetMapping("/view")
    public String listEntity(Model model) {
        return "adm/audit/view";
    }


    @PostMapping
    @ResponseBody
    public ResponseEntity<HttpStatus> changeAuditEntry(@RequestBody AuditConfig auditEntry) {
       auditCfgService.saveAuditConfig(auditEntry);
       ResponseEntity<HttpStatus> resp = new ResponseEntity<>(HttpStatus.NO_CONTENT);
       return resp;
    }
}
