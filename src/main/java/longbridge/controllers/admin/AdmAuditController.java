package longbridge.controllers.admin;

import longbridge.audit.AdmAuditService;
import longbridge.audit.AuditBlob;
import longbridge.audit.AuditId;
import longbridge.audit.RevisionDTO;
import longbridge.config.audits.ModifiedType;
import longbridge.config.audits.ModifiedType;
import longbridge.models.AuditConfig;
import longbridge.services.AuditConfigService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/admin/audit")
public class AdmAuditController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AdmAuditService admAuditService;


    @Autowired
    private AuditConfigService auditCfgService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false);

        // true passed to CustomDateEditor constructor means convert empty String to null
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping
    public String listRevisedEnties(Model model) {
        List<AuditConfig> auditConfig = auditCfgService.getEntities();
        model.addAttribute("entities", auditConfig);
        model.addAttribute("operations", admAuditService.getAllOperations());
        model.addAttribute("searchCriteria", new RevisionDTO());
        return "adm/audit2/index";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    DataTablesOutput<ModifiedType> getRevisons(DataTablesInput input, @ModelAttribute RevisionDTO searchCriteria) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ModifiedType> entityRev = null;
        entityRev = admAuditService.getRevisions(pageable, searchCriteria);
        DataTablesOutput<ModifiedType> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(entityRev.getContent());
        out.setRecordsFiltered(entityRev.getTotalElements());
        out.setRecordsTotal(entityRev.getTotalElements());
        return out;
    }


    @GetMapping("/revision")
    public @ResponseBody
    ResponseEntity<List<AuditBlob>> getRevDetail(AuditId id) {

        try {
            List<AuditBlob> revision = admAuditService.getRevision(id);

            return ResponseEntity.ok(revision);
        } catch (Exception e) {
            logger.error("Error", e);
            return ResponseEntity.badRequest().build();
        }

    }
}
