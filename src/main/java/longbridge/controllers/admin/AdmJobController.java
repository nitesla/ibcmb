package longbridge.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Longbridge on 6/28/2017.
 */
@Controller
@RequestMapping("admin/job")
public class AdmJobController {
@GetMapping
    public String manage(){
    return "adm/job/manage-job";
}
}
