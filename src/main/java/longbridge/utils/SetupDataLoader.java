//package longbridge.utils;
//
//import longbridge.models.*;
//import longbridge.repositories.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.Date;
//
//@Component
//public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
//
//    private boolean alreadySetup = false;
//
//    @Autowired
//    PermissionRepo permissionRepo;
//
//    @Autowired
//    RoleRepo roleRepo;
//    @Autowired
//    CodeRepo codeRepo;
//    @Autowired
//    RetailUserRepo retailUserRepo;
//
//    @Autowired
//    BCryptPasswordEncoder passwordEncoder;
//    @Autowired
//    AdminUserRepo adminUserRepo;
//
//    // API
//
//    @Override
//    @Transactional
//    public void onApplicationEvent(final ContextRefreshedEvent event) {
//        if (alreadySetup) {
//            return;
//        }
//
//        // == create initial permissions
//        Permission permission= new Permission();
//        permission.setName("VIEW_USER");
//        permission.setCode("09");
//        permission.setDescription("VIEW USERS IN THE SYSTEM");
//        permissionRepo.save(permission);
//
//        Permission permission2= new Permission();
//        permission2.setName("ADD_USER");
//        permission2.setCode("08");
//        permission2.setDescription("ADD USERS TO THE SYSTEM");
//        permissionRepo.save(permission2);
//        // == create initial roles
//
//        Role role= new Role();
//        role.setDescription("NO IDEA");
//        role.setUserType(UserType.ADMIN);
//        role.setEmail("brb@yahoo.com");
//        role.setName("Admin");
//        ArrayList<Permission> permissionArrayList = new ArrayList();
//        permissionArrayList.add(permissionRepo.findOne(1L));
//        roleRepo.save(role);
//
//        Role role2= new Role();
//        role2.setDescription("NO IDEA");
//        role2.setUserType(UserType.RETAIL);
//        role2.setEmail("ayoade_farooq@yahoo.com");
//        role2.setName("Retail");
//        ArrayList<Permission> permissionArrayList2 = new ArrayList();
//        permissionArrayList2.add(permissionRepo.findOne(2L));
//        role.setPermissions(permissionArrayList2);
//
//        roleRepo.save(role2);
//
//        AdminUser user= new AdminUser();
//        user.setAuthenticateMethod("");
//        user.setPassword(passwordEncoder.encode("password123"));
//        user.setUserType(UserType.ADMIN);
//        user.setUserName("bunmi01");
//        user.setFirstName("Adebunmi");
//        user.setLastName("Adeoye");
//        user.setEmail("Bunmi2007@cway.com");
//        user.setExpiryDate(new Date());
//        user.setLastLoginDate(new Date());
//        user.setRole(role);
//        user.setNoOfLoginAttempts(0);
//        adminUserRepo.save(user);
//
//
//        RetailUser user1 = new RetailUser();
//        user1.setNoOfLoginAttempts(0);
//        user1.setPassword(passwordEncoder.encode("password123"));
//        user1.setUserType(UserType.RETAIL);
//        user1.setRole(role2);
//        user1.setLastLoginDate(new Date());
//        user1.setExpiryDate(new Date());
//        user1.setEmail("ayoade@yahoo.com");
//        user1.setUserName("echez01");
//        user1.setLastName("ECHEZONA");
//        user1.setBirthDate(new Date());
//        user1.setDateCreated(new Date());
//        user1.setCustomerId("u09");
//        user1.setFirstName("OKEOWO");
//        user1.setStatus("OPEN");
//        user1.setExpiryDate(new Date());
//        retailUserRepo.save(user1);
//
//        alreadySetup = true;
//    }
//
//
//
//}