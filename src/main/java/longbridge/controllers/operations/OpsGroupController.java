package longbridge.controllers.operations;

import com.sun.javafx.sg.prism.NGShape;
import longbridge.dtos.UserGroupDTO;
import longbridge.models.UserGroup;
import longbridge.services.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Fortune on 5/3/2017.
 */

@Controller
@RequestMapping("ops/groups")
public class OpsGroupController {

    @Autowired
    UserGroupService userGroupService;

    @GetMapping("/new")
    public String addGroup(Model model){

        UserGroupDTO group = new UserGroupDTO();
        model.addAttribute("group", group);
        return "ops/unit/new-group";
    }


    @PostMapping
    public String createGroup(@ModelAttribute("group") UserGroupDTO userGroupDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()){

            return "/ops/group/new";
        }
        userGroupService.addGroup(userGroupDTO);
        return "redirect:/ops/groups";
    }


    @GetMapping("/operators")
    public String getOperators(){
        return "ops/group/operator";
    }

    @GetMapping("/{id}/operators/new")
    public String addOperator(@PathVariable Long id, Model model){
        UserGroupDTO group = userGroupService.getGroup(id);
        model.addAttribute("group",group);
        return "ops/group/add-operator";
    }
}
