package longbridge.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import longbridge.dtos.OperationsUserDTO;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordService;

@Controller
@RequestMapping("/general/operations/users")
public class OperationsUserContoller {
	
	private Logger logger= LoggerFactory.getLogger(this.getClass());
    @Autowired
    private  OperationsUserService operationsUserService;


    @Autowired
    PasswordService passwordService;

	@RequestMapping(path = "/find" )
    public @ResponseBody DataTablesOutput<OperationsUserDTO> getUsers(DataTablesInput input, OperationsUserDTO user){

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<OperationsUserDTO> operationsUsers = operationsUserService.findUsers(user, pageable);
        DataTablesOutput<OperationsUserDTO> out = new DataTablesOutput<OperationsUserDTO>();
        out.setDraw(input.getDraw());
        out.setData(operationsUsers.getContent());
        out.setRecordsFiltered(operationsUsers.getTotalElements());
        out.setRecordsTotal(operationsUsers.getTotalElements());

        return out;
    }
}
