package longbridge.audit;

import longbridge.models.AuditConfig;
import longbridge.services.AuditConfigService;
import longbridge.utils.DataTablesUtils;
import org.apache.commons.lang3.StringUtils;
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


@Controller
@RequestMapping(value = "/admin/audit/cfg")
public class AdmAuditConfigController {


    @Autowired
    AuditConfigService auditCfgService;


    @GetMapping()
    public String listSettings(Model model) {
        return "adm/setting/audit";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<AuditConfig> getAllCodes(DataTablesInput input, @RequestParam("csearch") String search) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AuditConfig> auditConf = null;
        if (StringUtils.isNoneBlank(search)) {
            auditConf = auditCfgService.findEntities(search, pageable);
        } else {
            auditConf = auditCfgService.getEntities(pageable);
        }
        DataTablesOutput<AuditConfig> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(auditConf.getContent());
        out.setRecordsFiltered(auditConf.getTotalElements());
        out.setRecordsTotal(auditConf.getTotalElements());
        return out;
    }


    @PostMapping
    @ResponseBody
    public ResponseEntity<HttpStatus> changeAuditEntry(@RequestBody AuditCfgDTO auditEntry, Model model) {
        auditCfgService.saveAuditConfig(auditEntry);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
