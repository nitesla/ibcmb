package longbridge.controllers.admin;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.config.audits.RevisedEntitiesUtil;
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
        return  "adm/audit/revisedview";
    }


    @GetMapping("/revised/entity/all")
    public @ResponseBody DataTablesOutput<ModifiedEntityTypeEntity> getAllRevisedEntity(DataTablesInput input)
    {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ModifiedEntityTypeEntity> auditConf=null;
        auditConf = auditCfgService.getRevisionEntities(pageable);
        DataTablesOutput<ModifiedEntityTypeEntity> out = new DataTablesOutput<ModifiedEntityTypeEntity>();
        out.setDraw(input.getDraw());
        out.setData(auditConf.getContent());
        out.setRecordsFiltered(auditConf.getTotalElements());
        out.setRecordsTotal(auditConf.getTotalElements());
        return out;
    }


    @GetMapping("/{id}/{classname}/view")
    public String getRevisionEntites(@PathVariable String id,@PathVariable String classname,Model model)
    {
        model.addAttribute("classname",classname);
        model.addAttribute("itemId",id);
        return  "adm/audit/entityDetails";
    }
    @GetMapping("/revised/entity/details")
    public @ResponseBody DataTablesOutput<ModifiedEntityTypeEntity> getRevisedEntityDetails(DataTablesInput input,WebRequest webRequest)
    {
//        logger.info("The id and class is {} {}",webRequest.getParameter("itemId"),webRequest.getParameter("classname"));
        Integer refId = Integer.parseInt(webRequest.getParameter("itemId"));
        String classname = webRequest.getParameter("classname");
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ModifiedEntityTypeEntity> auditConf=null;
        auditConf = auditCfgService.getRevisedDetailsForEntity(refId,classname,pageable);
        DataTablesOutput<ModifiedEntityTypeEntity> out = new DataTablesOutput<ModifiedEntityTypeEntity>();
        out.setDraw(input.getDraw());
        out.setData(auditConf.getContent());
        out.setRecordsFiltered(auditConf.getTotalElements());
        out.setRecordsTotal(auditConf.getTotalElements());
        return out;
    }



//    @GetMapping("/{id}/{classname}/view")
//    public  @ResponseBody DataTablesOutput<T> ListRevisedEnties(@PathVariable String id,@PathVariable String classname,DataTablesInput input)
//    {
//        Pageable pageable = DataTablesUtils.getPageable(input);
//        Page<T> revisionEntity=null;
//        revisionEntity=auditCfgService.revisedEntityDetails(classname,id,pageable);
////        DataTablesOutput<T> out=new DataTablesOutput<T>();
////        out.setDraw(input.getDraw());
////        out.setData(revisionEntity.getContent());
////        out.setRecordsFiltered(revisionEntity.getTotalElements());
////        out.setRecordsTotal(revisionEntity.getTotalElements());
////        return out;
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
