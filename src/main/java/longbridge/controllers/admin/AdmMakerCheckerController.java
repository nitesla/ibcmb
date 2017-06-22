package longbridge.controllers.admin;

import longbridge.dtos.CodeDTO;
import longbridge.models.AdminUser;
import longbridge.models.Verification;
import longbridge.services.AdminUserService;
import longbridge.services.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.security.Principal;
import java.util.List;

/**
 * Created by chiomarose on 19/06/2017.
 */


@Controller
@RequestMapping(value = "/admin/makerchecker")
public class AdmMakerCheckerController {

    @Autowired
    AdminUserService adminUserService;

    @Autowired
    VerificationService verificationService;

//    @Autowired
//    MakerCheckerService makerCheckerService;


    @GetMapping("/new")
    public String addCode(CodeDTO codeDTO) {
        return "adm/makerchecker/view";
    }

//    @GetMapping(path = "/all")
//    public @ResponseBody
//    DataTablesOutput<VerificationDTO> getAllCodes(DataTablesInput input)
//    {
//        Pageable pageable = DataTablesUtils.getPageable(input);
//        Page<VerificationDTO> codes = verificationService.getMakerCheckerPending(pageable);
//        DataTablesOutput<VerificationDTO> out = new DataTablesOutput<VerificationDTO>();
//        out.setDraw(input.getDraw());
//        out.setData(codes.getContent());
//        out.setRecordsFiltered(codes.getTotalElements());
//        out.setRecordsTotal(codes.getTotalElements());
//        return out;
//    }

//
//    @GetMapping("/pendingops")
//    public String getCodeTypes() {
//        return "adm/makerchecker/view";
//    }

    @GetMapping("/pendingops")
    public String getVerificationPage() {
        return "adm/makerchecker/view";
    }


    @GetMapping("/verificationops")
    public String getVerification() {
        return "adm/makerchecker/checker";
    }


    @GetMapping(path = "/allverification")
    public @ResponseBody
    DataTablesOutput<Verification> getAllVerification(DataTablesInput input, Principal principal) {
        AdminUser createdBy = adminUserService.getUserByName(principal.getName());
        Pageable pageable = DataTablesUtils.getPageable(input);
        List<Verification> codes = verificationService.getVerificationsForUser(createdBy);
        System.out.println("this is the code"+codes);
        DataTablesOutput<Verification> out = new DataTablesOutput<Verification>();
        out.setDraw(input.getDraw());
        out.setData(codes);
        out.setRecordsFiltered(codes.size());
        out.setRecordsTotal(codes.size());
        return out;
    }

    @GetMapping("/verification/{id}/view")
        public String getObjectsForVerification(){


        return "adm/makerchecker/details";
    }
}
