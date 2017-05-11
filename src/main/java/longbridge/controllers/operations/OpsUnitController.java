package longbridge.controllers.operations;

import longbridge.dtos.CodeDTO;
import longbridge.dtos.ContactDTO;
import longbridge.dtos.OperationsUserDTO;
import longbridge.dtos.UnitDTO;
import longbridge.dtos.UserGroupDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Contact;
import longbridge.models.OperationsUser;
import longbridge.models.Person;
import longbridge.models.Unit;
import longbridge.models.UserGroup;
import longbridge.services.CodeService;
import longbridge.services.OperationsUserService;
import longbridge.services.UnitService;

import java.util.*;

import longbridge.services.UserGroupService;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * Created by Fortune on 4/26/2017.
 */

@Controller
@RequestMapping("/ops/units")

public class OpsUnitController {

    @Autowired
    private CodeService codeService;

    @Autowired
    UnitService unitService;

    @Autowired
    UserGroupService userGroupService;

    @Autowired
    MessageSource messageSource;


    private Logger logger= LoggerFactory.getLogger(this.getClass());
    
    
    @Autowired
	private OperationsUserService operationsUserService;


    @GetMapping("/new")
    public String addUnit( Model model){
        model.addAttribute("unit", new Unit());
        Iterable<CodeDTO> units = codeService.getCodesByType("UNIT");
        model.addAttribute("units",units);
        return "ops/unit/add";
    }

    @PostMapping
    public String createUnit(WebRequest request, RedirectAttributes redirectAttributes, Model model, Locale locale){

        try {
		String contacts = request.getParameter("contacts");
		String name = request.getParameter("name");
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		List<ContactDTO> contactList = mapper.readValue(contacts, new TypeReference<List<ContactDTO>>() {
		});
		List<OperationsUserDTO> opList = new ArrayList<>();
		Iterator<ContactDTO> iterator = contactList.iterator();
		while (iterator.hasNext()) {
			ContactDTO contactDTO = iterator.next();
			if (!contactDTO.isExternal()) {
				// is opuser
				OperationsUserDTO operationsUserDTO = operationsUserService.getUser(contactDTO.getDt_RowId());
				opList.add(operationsUserDTO);
				iterator.remove();
			}
		}
		UserGroupDTO userGroup = new UserGroupDTO();
		userGroup.setName(name);
		userGroup.setContacts(contactList);
		userGroup.setUsers(opList);
		

		    String message = userGroupService.addGroup(userGroup);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/units";
        }
        catch (Exception ibe){
		    logger.error("Error creating group",ibe);
		    model.addAttribute("failure",messageSource.getMessage("group.add.failure",null,locale));
            return "ops/unit/add";
        }


    }

    @GetMapping
    public String getUnits(Model model){
        return "ops/unit/view";
    }

    @GetMapping("/all")
    public @ResponseBody DataTablesOutput<UnitDTO> getUnits(DataTablesInput input){
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<UnitDTO> units = unitService.getUnits(pageable);
        DataTablesOutput<UnitDTO> out = new DataTablesOutput<UnitDTO>();
        out.setDraw(input.getDraw());
        out.setData(units.getContent());
        out.setRecordsFiltered(units.getTotalElements());
        out.setRecordsTotal(units.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/edit")
    public String  editUnit(@PathVariable Long id, Model model){
        UnitDTO unit = unitService.getUnit(id);
        Iterable<CodeDTO> units = codeService.getCodesByType("UNIT");
        model.addAttribute("unit",unit);
        model.addAttribute("units",units);
        return "/ops/unit/edit";

    }

    @PostMapping("/update")
    public String updateUnit(@ModelAttribute("unit") UnitDTO unit, BindingResult result, RedirectAttributes redirectAttributes) throws Exception{
        if(result.hasErrors()) {
            return "ops/unit/new";
        }

        unitService.updateUnit(unit);
        logger.info("Units received at server: {]",unit.toString());
        redirectAttributes.addFlashAttribute("message", "Unit updated successfully");
        return "redirect:/ops/units";
    }



    @GetMapping("/{id}/delete")
    public String deleteUnit(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        unitService.deleteUnit(id);
        redirectAttributes.addFlashAttribute("message", "Unit deleted successfully");
        return "redirect:/ops/units";
    }
    
    @GetMapping("/find")
    public String  findOpsUser(Model model){
    	OperationsUser user = new OperationsUser();
    	model.addAttribute("operationsUser",user);
        return "/ops/unit/add-op";
    }
 
    @GetMapping("/contact/new")
    public String  addContact(Model model){
        return "/ops/unit/add-ex";
    }

}


