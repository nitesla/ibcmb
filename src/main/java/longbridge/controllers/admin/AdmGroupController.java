package longbridge.controllers.admin;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import longbridge.dtos.ContactDTO;
import longbridge.dtos.OperationsUserDTO;
import longbridge.dtos.UserGroupDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.OperationsUser;
import longbridge.services.CodeService;
import longbridge.services.OperationsUserService;
import longbridge.services.UserGroupService;
import longbridge.utils.DataTablesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


/**
 * Created by Fortune on 4/26/2017.
 */

@Controller
@RequestMapping("/admin/groups")

public class AdmGroupController {

    @Autowired
    private CodeService codeService;


    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private MessageSource messageSource;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private OperationsUserService operationsUserService;


    @GetMapping("/new")
    public String addGroup(Model model) {
        model.addAttribute("group", new UserGroupDTO());
        return "adm/group/add";
    }

    @PostMapping
    public String createGroup(@ModelAttribute("group") @Valid UserGroupDTO userGroup, BindingResult result, RedirectAttributes redirectAttributes, WebRequest request,Locale locale) {

        try {
            String contacts = request.getParameter("contactList");
            if (result.hasErrors()) {
                result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
                return "adm/group/add";
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
            userGroup.setContacts(contactList);
            userGroup.setUsers(opList);
            String message = userGroupService.addGroup(userGroup);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/groups";
        }
        catch (InternetBankingException ibe){
            logger.error("Error creating group", ibe);
            result.addError(new ObjectError("invalid", ibe.getMessage()));
            return "adm/group/add";
        }
        catch (Exception ibe) {
            logger.error("Error creating group", ibe);
            result.addError(new ObjectError("invalid", messageSource.getMessage("group.add.failure", null, locale)));
            return "adm/group/add";
        }


    }

    @GetMapping
    public String getGroups(Model model) {
        return "adm/group/view";
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
    @ResponseBody
    public DataTablesOutput<ContactDTO> getGroups(@PathVariable Long id, DataTablesInput input) {
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
        return "/adm/group/edit";

    }

    @PostMapping("/update")
    public String updateGroup(@ModelAttribute("group") @Valid UserGroupDTO userGroup, BindingResult result, RedirectAttributes redirectAttributes, WebRequest request,Locale locale) {

        try {
            String contacts = request.getParameter("contactList");
            if (result.hasErrors()) {
                result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
                return "adm/group/edit";
            }

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
            userGroup.setId(id);
            userGroup.setVersion(version);
            userGroup.setContacts(contactList);
            userGroup.setUsers(opList);


            String message = userGroupService.updateGroup(userGroup);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/admin/groups";
        }
        catch (InternetBankingException ibe){
            logger.error("Error creating group", ibe);
            result.addError(new ObjectError("invalid", ibe.getMessage()));
            return "adm/group/edit";
        }
        catch (Exception ibe) {
            logger.error("Error creating group", ibe);
            result.addError(new ObjectError("invalid", messageSource.getMessage("group.add.failure", null, locale)));
            return "adm/group/edit";
        }


    }

    @GetMapping("/{id}/delete")
    public String deleteGroup(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            String message = userGroupService.deleteGroup(id);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (InternetBankingException ibe) {
            logger.error("Error deleting group", ibe);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("group.delete.failure", null, locale));

        }
        return "redirect:/admin/groups";
    }

    @GetMapping("/find")
    public String findOpsUser(Model model) {
        OperationsUser user = new OperationsUser();
        model.addAttribute("operationsUser", user);
        return "/adm/group/add-op";
    }

    @GetMapping("/contact/new")
    public String addContact(Model model) {
        return "/adm/group/add-ex";
    }

}


