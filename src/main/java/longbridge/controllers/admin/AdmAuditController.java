package longbridge.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ayoade_farooq@yahoo.com on 4/18/2017.
 */
@Controller
@RequestMapping(value = "/admin/audit")
public class AdmAuditController {

    @Autowired
    EntityManager entityManager;

    @GetMapping()
    public String listAudit(Model model){
        List<String> tables = new ArrayList<>();
        entityManager.getEntityManagerFactory().getMetamodel().getEntities()
                .stream()
                .filter(i-> !i.getName().endsWith("AUD"))
                .filter(i-> !i.getName().endsWith("Entity"))
                .filter(i-> !i.getName().equalsIgnoreCase("AuditConfig"))
                .map(i -> i.getName()).collect(Collectors.toList())
                .forEach(e-> tables.add(e));


        //.forEach(i ->tables.add(i.getName()));

        Collections.sort(tables);
        model.addAttribute("tables",tables);

        return "adm/setting/audit";
    }
}
