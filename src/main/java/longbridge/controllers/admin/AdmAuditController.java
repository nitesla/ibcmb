package longbridge.controllers.admin;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.dtos.CodeDTO;
import longbridge.models.AuditRetrieve;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import longbridge.models.AuditConfig;
import longbridge.services.AuditConfigService;

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
    @GetMapping(path = "/admin/audit/revisedentity/{index}")
    public @ResponseBody DataTablesOutput<CustomRevisionEntity> getEntityRevisedDetials(DataTablesInput input,@PathVariable String entityName){
        logger.trace("entity name is {}",entityName);
        Pageable pageable = DataTablesUtils.getPageable(input);
        auditCfgService.revisedEntityDetails(entityName,pageable);
        Page<CustomRevisionEntity> auditConf = null;
        DataTablesOutput<CustomRevisionEntity> out = new DataTablesOutput<CustomRevisionEntity>();
        out.setDraw(input.getDraw());
        out.setData(auditConf.getContent());
        out.setRecordsFiltered(auditConf.getTotalElements());
        out.setRecordsTotal(auditConf.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/edit")
    public String ListRevisedEnties(@PathVariable Long id,Model model)
    {
        AuditConfig audit = auditCfgService.getAuditEntity(id);
        String entityName = audit.getEntityName();
        model.addAttribute("entityName", entityName);
        return "adm/audit/revisedview";
    }



//       try
//       {
//           Class<?> cls = Class.forName(entityName);
//           AuditRetrieve auditRetrieve=new AuditRetrieve();
//           auditRetrieve.getRevisions(cls);
//       }
//       catch (ClassNotFoundException e)
//       {
//           e.printStackTrace();
//       }




//    @GetMapping("revisedentity/{entityname}")
//    public @ResponseBody DataTablesOutput<T> getAllRevisedEntity(@PathVariable String name,DataTablesInput input)
//    {
//        List<CustomRevisionEntity> revisedEntity=auditCfgService.revisedEntityDetails(name);
//       // List<Object> mergedClassDetails = new ArrayList<Object>(entityDetails.values());
//
//
//    }


//    @GetMapping("/{id}/edit")
//    public String ListRevisedEntity<T> getAudited(@PathVariable Long id,Model model)
//    {
//        AuditConfig audit = auditCfgService.getAuditEntity(id);
//        String entityName = audit.getEntityName();
//       // String entityNames =entityName;
//        model.addAttribute(entityName);
//
//        return "all/auditedview";
//
//
//
////        Map<String, Object> entityDetails = auditCfgService.revisedEntityDetails(entityNames);
////        List<Object> mergedClassDetails = new ArrayList<Object>(entityDetails.values());
////        System.out.println("This the merge Class Details  @@@@@ " +mergedClassDetails);
//
//        //return "adm/audit/view";
//    }


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
