package longbridge.controllers.admin;

import longbridge.dtos.OperationsUserDTO;
import longbridge.services.OperationsUserService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/operations/users")
public class AdmUserController {


    @Autowired
    private OperationsUserService operationsUserService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping(path = "/find")
    public
    @ResponseBody
    DataTablesOutput<OperationsUserDTO> getUsers(DataTablesInput input, OperationsUserDTO user) {

        logger.info("Users to search: {}", user.toString());

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<OperationsUserDTO> operationsUsers = operationsUserService.findUsers(user, pageable);
        DataTablesOutput<OperationsUserDTO> out = new DataTablesOutput<>();
        out.setDraw(input.getDraw());
        out.setData(operationsUsers.getContent());
        logger.info("Users found: {}", operationsUsers.getContent().toString());
        out.setRecordsFiltered(operationsUsers.getTotalElements());
        out.setRecordsTotal(operationsUsers.getTotalElements());

        return out;
    }
}
