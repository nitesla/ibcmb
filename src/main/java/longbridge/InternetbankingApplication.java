package longbridge;

import longbridge.models.*;
import longbridge.repositories.*;
import longbridge.services.implementations.AuditConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
public class InternetbankingApplication implements CommandLineRunner {


	@Autowired
	PermissionRepo permissionRepo;

  @Autowired
	RoleRepo roleRepo;
  @Autowired
    CodeRepo codeRepo;

@Autowired
BCryptPasswordEncoder passwordEncoder;
@Autowired
    AdminUserRepo adminUserRepo;

	public static void main(String[] args) {
		SpringApplication.run(InternetbankingApplication.class, args);
	}



	@Override
   // @Transactional
	public void run(String... strings) throws Exception {

/*
		Permission permission= new Permission();
		permission.setName("ADMIN");
		permission.setCode("09");
		permission.setDescription("TRAIL AND ERROR");

		permissionRepo.save(permission);


		Role role= new Role();
		role.setDescription("NO IDEA");
		role.setUserType(UserType.ADMIN);
		role.setEmail("ayoade_farooq@yahoo.com");
		role.setName("SUPER USER");

		ArrayList<Permission> permissionArrayList = new ArrayList();
		permissionArrayList.add(permissionRepo.findOne(1L));
		role.setPermissions(permissionArrayList);

		roleRepo.save(role);

        AdminUser user= new AdminUser();
        user.setAuthenticateMethod("");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setUserType(UserType.ADMIN);
        user.setUserName("bunmi01");
        user.setFirstName("Adebunmi");
        user.setLastName("Adeoye");
        user.setEmail("Bunmi2007@cway.com");
        user.setExpiryDate(new Date());
        user.setLastLoginDate(new Date());
        user.setRole(roleRepo.findOne(5L));
        user.setNoOfLoginAttempts(0);
        adminUserRepo.save(user);



Code code= new Code();

code.setType("d");
code.setDescription("hello");
  codeRepo.save(code);


        Code code= new Code();

        code.setType("d");
        code.setDescription("hello");
        codeRepo.save(code);

*/
        System.out.println("****************  "  +UserType.RETAIL.toString());
    }

}
