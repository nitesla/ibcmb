package longbridge.controllers.admin;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.MakerCheckerDTO;
import longbridge.dtos.VerificationDTO;
import longbridge.models.MakerChecker;
import longbridge.services.MakerCheckerService;
import longbridge.services.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chiomarose on 19/06/2017.
 */

@Controller
@RequestMapping(value = "/admin/makerchecker")
public class AdmMakerCheckerController {

//    @Autowired
//    MakerCheckerService makerCheckerService;

    @Autowired
    VerificationService verificationService;
    @GetMapping("/new")
    public String addCode(CodeDTO codeDTO) {
        return "adm/makerchecker/view";
    }
//
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


    @GetMapping("/pendingops")
    public String getCodeTypes() {
        return "adm/makerchecker/view";
    }





}
