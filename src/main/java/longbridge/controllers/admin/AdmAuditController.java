package longbridge.controllers.admin;

import longbridge.config.audits.CustomRevisionEntity;
import longbridge.config.audits.ModifiedEntityTypeEntity;
import longbridge.config.audits.RevisedEntitiesUtil;
import longbridge.dtos.AdminUserDTO;
import longbridge.dtos.AuditDTO;
import longbridge.dtos.CodeDTO;
//import longbridge.dtos.RevisionInfo;
import longbridge.dtos.VerificationDTO;
import longbridge.models.AuditRetrieve;
import longbridge.models.User;
import longbridge.models.Verification;
import longbridge.security.userdetails.CustomUserPrincipal;
import longbridge.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.formula.functions.Value;
import org.codehaus.jettison.json.JSONObject;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static longbridge.utils.StringUtil.convertFieldToTitle;


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

        logger.info("the entity name is {}",entityName);
        model.addAttribute("entityName",entityName);
       List<AuditConfig> auditConfig=auditCfgService.getEntities();
        model.addAttribute("entities",auditConfig);
        String className = PACKAGE_NAME+entityName;
        Class<?> cl = null;
        try {
            List<String> classFields =  new ArrayList<>();
            List<String> headers =  new ArrayList<>();
            cl = Class.forName(className);
            Field[] declaredFields = cl.getDeclaredFields();

            logger.info("the fields of the {} are {}",entityName,declaredFields);
            for (Field field:declaredFields) {

                String fieldName = StringUtil.extractedFieldName(field.toString());

                Annotation[] annotations = field.getAnnotations();
                boolean fieldDiplay = true;
                for (Annotation annotation: annotations) {
                    if(annotation.toString().contains("ManyToOne")||annotation.toString().contains("ManyToMany")||annotation.toString().contains("OneToMany")){
                        fieldDiplay = false;
                        break;
                    }
                }
                if(!fieldDiplay){
                    continue;
                }
                if(fieldName.equalsIgnoreCase("serialVersionUID")) {
                    continue;
                }
                if(entityName.equalsIgnoreCase("CorporateUser")){
                    if(fieldName.equalsIgnoreCase("corporate") || fieldName.equalsIgnoreCase("tempPassword") ){
                        continue;
                    }
                }

                headers.add(convertFieldToTitle(fieldName));
                classFields.add("entityDetails." + fieldName);
            }
//            if(!cl.getSuperclass().toString().contains("AbstractEntity")) {
//                for (Field field : cl.getSuperclass().getDeclaredFields()) {
//                    String fieldName = StringUtil.extractedFieldName(field.toString());
//                    Annotation[] annotations = field.getAnnotations();
//                    boolean fieldDiplay = true;
//                    for (Annotation annotation: annotations) {
//                        if(annotation.toString().contains("ManyToOne")||annotation.toString().contains("ManyToMany")||annotation.toString().contains("OneToMany")){
//                            fieldDiplay = false;
//                            break;
//                        }
//                    }
//                    if(!fieldDiplay){
//                        continue;
//                    }
//                    headers.add(convertFieldToTitle(fieldName));
//                    classFields.add("entityDetails." + fieldName);
//                }
//            }
            if(!classFields.isEmpty()) {
                model.addAttribute("fields", classFields);
            }else {
                model.addAttribute("fields", null);
            }
            model.addAttribute("headers",headers);
            model.addAttribute("headerSize",headers.size());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        logger.info("the superclass {}",cl.getSuperclass());

        return "adm/audit/entityRevision";
    }
    //    @GetMapping(path = "all/entityname")
//    public @ResponseBody DataTablesOutput<AuditConfig> getAllEntities(DataTablesInput input,WebRequest webRequest)
//    {
//        Pageable pageable=DataTablesUtils.getPageable(input);
//        Page<AuditConfig> auditConfig=null;
//        auditConfig=auditCfgService.getEntities(pageable);
//        DataTablesOutput<AuditConfig> out=new DataTablesOutput<>();
//        out.setDraw(input.getDraw());
//        out.setData(auditConfig.getContent());
//        out.setRecordsFiltered(auditConfig.getTotalElements());
//        out.setRecordsTotal(auditConfig.getTotalElements());
//        return out;
//    }
    @GetMapping("/revised/entity")
    public String ListAllRevisedEnties()

    {
        return "adm/audit/revisedview";
    }


    @GetMapping("/entity/name/details")
    public @ResponseBody DataTablesOutput<AuditDTO> getAllRevisedEntity(DataTablesInput input,@RequestParam("className") String className,@RequestParam("csearch") String csearch)
    {
//        @RequestParam("className") String className,@RequestParam("csearch") String csearch
//        String className = "TransRequest";
//        String csearch = "";
        logger.info("The class name {}",className);
        logger.info("TO search {}",csearch);
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AuditDTO> auditDTOs = null;
        if(csearch==null || csearch.equalsIgnoreCase("")){
            auditDTOs = auditCfgService.revisedEntity(className,pageable);
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



//    @GetMapping("/revised/entity/all")
//    public @ResponseBody DataTablesOutput<ModifiedEntityTypeEntity> getAllRevisedEntity(DataTablesInput input,@RequestParam("csearch") String search)
//    {
//      //  Pageable pageable = DataTablesUtils.getPageable(input);
//        Pageable pageables = DataTablesUtils.getPageable(input);
//        Page<ModifiedEntityTypeEntity> audit = null;
//        if(StringUtils.isNoneBlank(search))
//        {
//            audit=auditCfgService.getRevisionEntities(search,pageables);
//        }
//        else
//        {
//            audit=auditCfgService.getRevisionEntitiesByDate(pageables);
//        }
//        DataTablesOutput<ModifiedEntityTypeEntity> out = new DataTablesOutput<ModifiedEntityTypeEntity>();
//        out.setDraw(input.getDraw());
//        out.setData(audit.getContent());
//        out.setRecordsFiltered(audit.getTotalElements());
//        out.setRecordsTotal(audit.getTotalElements());
//        return out;
//    }

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
