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
import longbridge.models.UserGroup;
import longbridge.services.CodeService;
import longbridge.services.OperationsUserService;

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
@RequestMapping("/ops/groups")

public class OpsGroupController {

    @Autowired
    private CodeService codeService;


    @Autowired
    UserGroupService userGroupService;

    @Autowired
    MessageSource messageSource;


    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private OperationsUserService operationsUserService;


    @GetMapping("/new")
    public String addGroup(Model model) {
        model.addAttribute("group", new UserGroupDTO());
        return "ops/group/add";
    }

    @PostMapping
    public String createGroup(WebRequest request, RedirectAttributes redirectAttributes, Model model, Locale locale) {

        try {
            String contacts = request.getParameter("contacts");
            String name = request.getParameter("name");
            if("".equals(name)){
                model.addAttribute("group", new UserGroupDTO());
                model.addAttribute("failure","Group name is required");
                return "ops/group/add";
            }
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
            return "redirect:/ops/groups";
        } catch (Exception ibe) {
            logger.error("Error creating group", ibe);
            model.addAttribute("failure", messageSource.getMessage("group.add.failure", null, locale));
            return "ops/group/add";
        }


    }

    @GetMapping
    public String getGroups(Model model) {
        return "ops/group/view";
    }

    @GetMapping("/all")
    public
    @ResponseBody
    DataTablesOutput<UserGroupDTO> getGroups(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<UserGroupDTO> groups = userGroupService.getGroups(pageable);
        DataTablesOutput<UserGroupDTO> out = new DataTablesOutput<UserGroupDTO>();
        out.setDraw(input.getDraw());
        out.setData(groups.getContent());
        out.setRecordsFiltered(groups.getTotalElements());
        out.setRecordsTotal(groups.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/all")
    public
    @ResponseBody
    DataTablesOutput<ContactDTO> getGroups(@PathVariable Long id, DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        List<ContactDTO> groups = userGroupService.getContacts(id);
        DataTablesOutput<ContactDTO> out = new DataTablesOutput<ContactDTO>();
        out.setDraw(input.getDraw());
        out.setData(groups);
        out.setRecordsFiltered(groups.size());
        out.setRecordsTotal(groups.size());
        return out;
    }

    @GetMapping("/{id}/edit")
    public String editGroup(@PathVariable Long id, Model model) {
        UserGroupDTO group = userGroupService.getGroup(id);
        model.addAttribute("group", group);
        return "/ops/group/edit";

    }


    @PostMapping("/update")
    public String updateGroup(WebRequest request, RedirectAttributes redirectAttributes, Model model, Locale locale) {

        try {
            String contacts = request.getParameter("contacts");
            String name = request.getParameter("name");

            Long id = Long.parseLong(request.getParameter("id"));
            int version = Integer.parseInt(request.getParameter("version"));
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
            userGroup.setId(id);
            userGroup.setVersion(version);
            userGroup.setName(name);
            userGroup.setContacts(contactList);
            userGroup.setUsers(opList);


            String message = userGroupService.updateGroup(userGroup);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/groups";
        } catch (Exception ibe) {
            logger.error("Error creating group", ibe);
            model.addAttribute("failure", messageSource.getMessage("group.update.failure", null, locale));
            return "ops/group/edit";
        }


    }

    @GetMapping("/{id}/delete")
    public String deleteGroup(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            String message = userGroupService.deleteGroup(id);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/ops/groups";
        } catch (InternetBankingException ibe) {
            logger.error("Error deleting group", ibe);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("group.delete.failure", null, locale));
            return "redirect:/ops/groups";

        }
    }

    @GetMapping("/find")
    public String findOpsUser(Model model) {
        OperationsUser user = new OperationsUser();
        model.addAttribute("operationsUser", user);
        return "/ops/group/add-op";
    }

    @GetMapping("/contact/new")
    public String addContact(Model model) {
        return "/ops/group/add-ex";
    }

}


