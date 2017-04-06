package longbridge.controllers.admin;

import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Controller for maintaining application {@link longbridge.models.Setting}
 * Created by LB-PRJ-020 on 4/5/2017.
 */
@Controller
@RequestMapping("/setting")
public class AdmSettingController {

    @GetMapping()
    public String listSettings(Model model){
        return "listsettings";
    }

    @GetMapping("/{settingId}")
    public String viewSetting(@PathVariable Long id, Model model){
        return "viewsetting";
    }

    @GetMapping("/edit/{settingId}")
    public String editSetting(@PathVariable Long id, Model model){
        return  "editsetting";
    }

    @PostMapping("/edit/{settingId}")
    public String updateSetting(@PathVariable Long id, Model model){
        return "editsetting";
    }

    @PostMapping()
    public String createSetting(@PathVariable Long id, Model model){
        return "redirect: /listSettings";
    }

    @GetMapping("/add")
    public String newSetting(Model model){
        return "addSetting";
    }
}

