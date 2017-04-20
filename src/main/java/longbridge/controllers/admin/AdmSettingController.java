package longbridge.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Controller for maintaining application {@link longbridge.models.Setting}
 * Created by LB-PRJ-020 on 4/5/2017.
 */
@Controller
@RequestMapping("/admin/setting")
public class AdmSettingController {


@Autowired
    private ConfigurationService configurationService;
    @GetMapping()
    public String listSettings(Model model){


        return "adm/setting/settings";
    }





@GetMapping(path = "/fiad")
    public @ResponseBody SettingDTO getSetting(){
        SettingDTO fiad = configurationService.getSettingByName("FI_ADDRESS");
        return fiad;
    }
    @GetMapping("/{settingId}")
    public String viewSetting(@PathVariable Long id, Model model){
        return "setting/details";
    }


    @GetMapping("/edit/{settingId}")
    public String editSetting(@PathVariable Long id, Model model){
        return "setting/edit";
    }

    @PostMapping("/edit/{settingId}")
    public String updateSetting(@PathVariable Long id, Model model){
        return "redirect: /";
    }

    @PostMapping()
    public String createSetting(@PathVariable Long id, Model model){
        return "redirect: /setting";
    }

    @GetMapping("/add")
    public String newSetting(Model model){
        return "setting/add";
    }
}

