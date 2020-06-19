package longbridge.controllers.admin;

import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.config.audits.RevisedEntitiesUtil;
import longbridge.dtos.AuditDTO;
import longbridge.dtos.AuditSearchDTO;
import longbridge.models.AuditConfig;
import longbridge.services.AuditConfigService;
import longbridge.utils.DataTablesUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

//import longbridge.dtos.RevisionInfo;


/**
 * Created by ayoade_farooq@yahoo.com on 4/18/2017.
 */
@Controller
@RequestMapping(value = "/admin/audit")
public class AdmAuditController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AuditConfigService auditCfgService;
    private static final String PACKAGE_NAME = "longbridge.models.";



    @GetMapping()
    public String listSettings(Model model)
    {

        return "adm/setting/audit";
    }

    @GetMapping(path = "/all")
    public @ResponseBody DataTablesOutput<AuditConfig> getAllCodes(DataTablesInput input,@RequestParam("csearch") String search){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AuditConfig> auditConf;
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

    @GetMapping("/revised/entities")
    public String ListRevisedEnties(Model model)
    {
        List<AuditConfig> auditConfig=auditCfgService.getEntities();

        model.addAttribute("entities",auditConfig);
        return "adm/audit/view";
    }
    @GetMapping("/revised/{entityName}")
    public String revisedEntity(@PathVariable String entityName, Model model)
    {
        model.addAttribute("entityName",entityName);
        List<AuditConfig> auditConfig=auditCfgService.getEntities();
        model.addAttribute("entities",auditConfig);
        Map<String, Object> formatedEntityDetails = auditCfgService.getFormatedEntityDetails(entityName);
        model.addAttribute("fields", formatedEntityDetails.get("fields"));
        model.addAttribute("headers",formatedEntityDetails.get("headers"));
        model.addAttribute("headerSize",formatedEntityDetails.get("headerSize"));
        return "adm/audit/entityRevision";
    }
    @GetMapping("/revised/entity")
    public String ListAllRevisedEnties()

    {
        return "adm/audit/revisedview";
    }
    @GetMapping("/entity/index")
    public String viewRevisedEnties(Model model){
RevisedEntitiesUtil entitiesUtil = new RevisedEntitiesUtil();
//        RevisedEntitiesUtil.getSearchedModifiedEntity("AdminUser",null,"");
//        List<ModifiedEntityTypeEntity> entityTypeEntities = auditCfgService.getAll();
        List<AuditConfig> auditConfig=auditCfgService.getEntities();
        model.addAttribute("entities",auditConfig);
        return "adm/audit/new/auditIndex";
    }


    @GetMapping("/entity/name/details")
    public @ResponseBody DataTablesOutput<AuditDTO> getAllRevisedEntity(DataTablesInput input,Model model,@RequestParam("className") String className,@RequestParam("csearch") String csearch)
    {
        logger.info("The class name {}",className);
        logger.info("TO search {}",csearch);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AuditDTO> auditDTOs = null;
        if(csearch==null || csearch.equalsIgnoreCase("")){
            auditDTOs = auditCfgService.revisedEntityByQuery(className,pageable);
//            auditDTOs = auditCfgService.revisedEntity(className,pageable);
        }else {
            auditDTOs = auditCfgService.searchRevisedEntity(className,pageable,csearch);
        }
        DataTablesOutput<AuditDTO> out = new DataTablesOutput<AuditDTO>();
        out.setDraw(input.getDraw());
        out.setData(auditDTOs.getContent());
        out.setRecordsFiltered(auditDTOs.getTotalElements());
        out.setRecordsTotal(auditDTOs.getTotalElements());
        return out;
    }



    @GetMapping("/revised/entity/all")
    public @ResponseBody DataTablesOutput<ModifiedEntityTypeEntity> getAllRevisedEntity(DataTablesInput input,@RequestParam("csearch") String search)
    {
      //  Pageable pageable = DataTablesUtils.getPageable(input);
        logger.info("the search current "+search);
        Pageable pageables = DataTablesUtils.getPageable(input);
        Page<ModifiedEntityTypeEntity> audit = null;
        if(StringUtils.isNoneBlank(search))
        {
            audit=auditCfgService.getRevisionEntities(search,pageables);
        }
        else
        {
            audit=auditCfgService.getRevisionEntitiesByDate(pageables);
        }
        DataTablesOutput<ModifiedEntityTypeEntity> out = new DataTablesOutput<ModifiedEntityTypeEntity>();
        out.setDraw(input.getDraw());
        out.setData(audit.getContent());
        out.setRecordsFiltered(audit.getTotalElements());
        out.setRecordsTotal(audit.getTotalElements());
        return out;
    }


    @GetMapping("/{id}/{classname}/view")
    public String revisionEntites(@PathVariable String id,@PathVariable String classname,Model model)
    {
        logger.info("class {} and id is {}",classname,id);
        model.addAttribute("classname",classname);
        model.addAttribute("itemId",id);
        return  "adm/audit/entityDetails";
    }


    @GetMapping("/revised/entity/details")
    public @ResponseBody DataTablesOutput<ModifiedEntityTypeEntity> getRevisedEntityDetails(DataTablesInput input,WebRequest webRequest)
    {
        Integer refId = parseInt(webRequest.getParameter("itemId"));
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


    @GetMapping("/{revisionId}/{classname}/view/compare")
    public String compareEntityDetails(@PathVariable String[] revisionId,@PathVariable String classname,Model model)
    {
        model.addAttribute("classname",classname);
        model.addAttribute("itemId",revisionId[0]);
        Map entityPastDetails = RevisedEntitiesUtil.getEntityPastDetails(classname, revisionId);
        logger.info("entity details is {} @@@@@ ",entityPastDetails.get("keys"));
        Map<String,Object> m =null;
        model.addAttribute("pastDetails", entityPastDetails.get("pastDetails"));
        model.addAttribute("currentDetails",(entityPastDetails.get("currentDetails")));
        model.addAttribute("headers",(entityPastDetails.get("keys")));
        return  "adm/audit/entityDetails";
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
        return "adm/audit/viewbkp";
    }


    @PostMapping
    @ResponseBody
    public ResponseEntity<HttpStatus> changeAuditEntry(@RequestBody AuditConfig auditEntry) {
        auditCfgService.saveAuditConfig(auditEntry);
        ResponseEntity<HttpStatus> resp = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return resp;
    }
    @GetMapping("/revised/entity/search")
    public @ResponseBody DataTablesOutput<ModifiedEntityTypeEntity> searchRevisedEntity(
            DataTablesInput input,@RequestParam("id") String id, @RequestParam("entityName") String entityName,
            @RequestParam("fromDate") String fromDate, @RequestParam("endDate") String endDate,
            @RequestParam("ipAddress") String ipAddress, @RequestParam("lastChangedBy") String lastChangedBy, @RequestParam("username") String username)
    {
        logger.info("the search details entityName {},fromDate {}, endDate {}, ipAddress {}, id {} ",entityName,fromDate,endDate,ipAddress,id);

        Pageable pageables = DataTablesUtils.getPageable(input);
        Page<ModifiedEntityTypeEntity> audit = null;
        if(!StringUtils.isNoneBlank(entityName)&& !StringUtils.isNoneBlank(fromDate)
                && !StringUtils.isNoneBlank(endDate)&& !StringUtils.isNoneBlank(ipAddress)
                && !StringUtils.isNoneBlank(lastChangedBy)){
            audit=auditCfgService.getRevisionEntitiesByDate(pageables);

        }
        else{
            AuditSearchDTO auditSearchDTO = new AuditSearchDTO(id,entityName,fromDate,endDate,ipAddress,lastChangedBy,username);
            audit=  auditCfgService.searchMod(pageables,auditSearchDTO);
            logger.info("the search query is {}",auditSearchDTO);
        }
        DataTablesOutput<ModifiedEntityTypeEntity> out = new DataTablesOutput<ModifiedEntityTypeEntity>();
        out.setDraw(input.getDraw());
        out.setData(audit.getContent());
        out.setRecordsFiltered(audit.getTotalElements());
        out.setRecordsTotal(audit.getTotalElements());
        return out;
    }
    //for the index page for the onclick event.
    @GetMapping("/{id}/{classname}/view/details")
    public String revisedEntityDetsils(@PathVariable String id,@PathVariable String classname,Model model)
    {
        logger.info("class {} and id is {}",classname,id);
        Map<String, Object> entityDetailsById = RevisedEntitiesUtil.getEntityDetailsById(classname, Integer.parseInt(id));
        model.addAttribute("entityId",entityDetailsById.get("id"));
        model.addAttribute("classname",classname);
        model.addAttribute("itemId",id);
        return  "adm/audit/new/entityIdDetails";
    }
    @GetMapping("/{revisionId}/{classname}/{entityId}/view/details/compare")
    public String compareEntityDetailsOfId(@PathVariable String[] revisionId,@PathVariable String classname,@PathVariable String entityId,Model model)
    {
        if(revisionId.length>1) {
            logger.info(" the revision ids is {}", revisionId[1]);
        }
        model.addAttribute("classname",classname);
        model.addAttribute("itemId",revisionId[0]);
        Map<String,List<String>> entityPastDetails = RevisedEntitiesUtil.getEntityPastDetails(classname, revisionId);
        List<String> keys = entityPastDetails.get("keys");
        model.addAttribute("entityId",entityId);
        model.addAttribute("pastDetails", entityPastDetails.get("pastDetails"));
        model.addAttribute("currentDetails",(entityPastDetails.get("currentDetails")));
        model.addAttribute("headers", keys);
        model.addAttribute("selectedItemId", entityPastDetails.get("selectedItemId"));

        return  "adm/audit/new/entityIdDetails";
    }
    @GetMapping("/revised/entity/details/id")
    public @ResponseBody DataTablesOutput<ModifiedEntityTypeEntity> getRevisedEntityDetailsById(DataTablesInput input,
                                                                                                @RequestParam("id") String id, @RequestParam("entityName") String entityName,
                                                                                                @RequestParam("fromDate") String fromDate, @RequestParam("endDate") String endDate,
                                                                                                @RequestParam("ipAddress") String ipAddress, @RequestParam("lastChangedBy") String lastChangedBy)
    {
        logger.info("the search details entityName {},fromDate {}, endDate {}, ipAddress {}, id {} ",entityName,fromDate,endDate,ipAddress,id);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ModifiedEntityTypeEntity> auditConf=null;
        AuditSearchDTO auditSearchDTO = new AuditSearchDTO(id,entityName,fromDate,endDate,ipAddress,lastChangedBy);
        logger.info("the search query is {}",auditSearchDTO);
        auditConf=  auditCfgService.searchMod(pageable,auditSearchDTO);
        DataTablesOutput<ModifiedEntityTypeEntity> out = new DataTablesOutput<ModifiedEntityTypeEntity>();
        out.setDraw(input.getDraw());
        out.setData(auditConf.getContent());
        out.setRecordsFiltered(auditConf.getTotalElements());
        out.setRecordsTotal(auditConf.getTotalElements());
        return out;
    }
}
