package longbridge.controllers.admin;

import longbridge.models.AdminUser;
import longbridge.services.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Wunmi on 31/03/2017.
 */
@RestController


@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    AdminUserService adminUserService;

    @GetMapping("/newUser")
    public String addUser(){
        return "addUser";
    }

    @PostMapping("/newUser")
    public String createUser( AdminUser adminUser, Model model) throws Exception{
        adminUserService.addUser(adminUser);
        model.addAttribute("success","Admin user created successfully");
        return "createUser";
    }

    @PostMapping("/getAdminUsers")
    public String getAllAdminUsers(Model model){
        Iterable<AdminUser> adminUserList=adminUserService.getUsers();
        model.addAttribute("adminUserList",adminUserList);
        return "getAdminUsers";
    }

    @PostMapping("/")

}
