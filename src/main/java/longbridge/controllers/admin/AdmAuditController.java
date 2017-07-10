package longbridge.controllers.admin;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.dtos.CodeDTO;
//import longbridge.dtos.RevisionInfo;
import longbridge.dtos.VerificationDTO;
import longbridge.models.AuditRetrieve;
import longbridge.models.User;
import longbridge.models.Verification;
import longbridge.security.userdetails.CustomUserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.formula.functions.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import longbridge.models.AuditConfig;
import longbridge.services.AuditConfigService;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by ayoade_farooq@yahoo.com on 4/18/2017.
 */
@Controller
@RequestMapping(value = "/admin/audit")
public class AdmAuditController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AuditConfigService auditCfgService;

    
    @GetMapping()
	public String listSettings(Model model)
    {
		return "adm/setting/audit";
	}
    
    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<AuditConfig> getAllCodes(DataTablesInput input,@RequestParam("csearch") String search){
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




    @GetMapping("/revised/entity")
    public String ListRevisedEnties(){
        return "adm/audit/revisedview";
    }


    @GetMapping("/revised/{revisedid}")
    public  String getRevisionId(@PathVariable Long revisedid)
    {
        logger.info("this is the id",+revisedid);
        return  "adm/audit/revisedview";
    }


    @GetMapping("/revised/entity/all")
    public @ResponseBody DataTablesOutput<ModifiedEntityTypeEntity> getAllRevisedEntity(DataTablesInput input,@RequestParam("csearch") String search)
    {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ModifiedEntityTypeEntity> auditConf=null;
        if (StringUtils.isNoneBlank(search))
        {
            auditConf = auditCfgService.getRevisionEntities(search,pageable);
        }
        else
        {
            auditConf = auditCfgService.getRevisionEntities(pageable);
        }
        DataTablesOutput<ModifiedEntityTypeEntity> out = new DataTablesOutput<ModifiedEntityTypeEntity>();
      //  Page<ModifiedEntityTypeEntity> auditConf = auditCfgService.getRevisionEntities(pageable);
        out.setDraw(input.getDraw());
        out.setData(auditConf.getContent());
        out.setRecordsFiltered(auditConf.getTotalElements());
        out.setRecordsTotal(auditConf.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/{classname}/view")
    public String ListRevisedEnties(@PathVariable String id,@PathVariable String classname, Model model)
    {
        logger.info("entity number is {}",id);
        logger.info("entity class is {}",classname);
        auditCfgService.revisedEntityDetails(classname,Integer.parseInt(id),null);
//        AuditConfig audit = auditCfgService.getAuditEntity(id);
//        String entityName = audit.getEntityName();
//        logger.info("entity name is {}",entityName);
//        model.addAttribute("entityName", entityName);
        return "adm/audit/view";
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
