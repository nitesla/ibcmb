package longbridge;

import longbridge.api.Rate;
import longbridge.config.SpringClassScanner;
import longbridge.config.makerchecker.MakerCheckerInitializer;
import longbridge.jobs.CronJobs;
import longbridge.models.MakerChecker;
import longbridge.models.Permission;
import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.repositories.MakerCheckerRepo;
import longbridge.repositories.PermissionRepo;
import longbridge.services.IntegrationService;

import longbridge.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.util.Date;
import java.util.List;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)

public class InternetbankingApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private CronJobs cronJobs;

    @Autowired
    private PermissionRepo permissionRepo;

    @Autowired
    private MakerCheckerRepo makerCheckerRepo;

//    @Autowired
//    private SpringClassScanner springClassScanner;

    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }

    @Override
    public void run(String... strings) throws Exception {
        cronJobs.startJob();
        //springClassScanner.afterPropertiesSet();
//      securityService.createEntrustUser("wumiTofu01","Wunmi baba ",true);
//        securityService.addUserContacts("soluwawunmi@gmail.com","07038810752",true,"wumiTofu01");
//         securityService.sendOtp("wumiTofu01");
//        System.out.println("Your password is "+passwordEncoder.encode("password123"));
//        integrationService.getAccountStatements("1234",new Date(),new Date());
//        System.out.println("end process");

//        Iterable<Permission> permissionList = permissionRepo.findByUserType("ADMIN");
//
//        for (Permission permission : permissionList)
//        {
//            MakerChecker makerChecker = new MakerChecker();
//            makerChecker.setDelFlag("N");
//            makerChecker.setVersion(0);
//            makerChecker.setCode(permission.getCode());
//            makerChecker.setEntityName(permission.getName());
//            makerChecker.setUserType(permission.getUserType());
//            makerChecker.setEnabled("N");
//            makerCheckerRepo.save(makerChecker);
//        }

//        Iterable<Permission> permissionList1 = permissionRepo.findByUserType("OPERATIONS");
//
//        for (Permission permission : permissionList1)
//        {
//            MakerChecker makerChecker = new MakerChecker();
//            makerChecker.setDelFlag("N");
//            makerChecker.setVersion(0);
//            makerChecker.setCode(permission.getCode());
//            makerChecker.setEntityName(permission.getName());
//            makerChecker.setUserType(permission.getUserType());
//            makerChecker.setEnabled("N");
//            makerCheckerRepo.save(makerChecker);
//        }

//    Rate    rate=  integrationService.getFee("NIP");
//        System.out.println("Rate "+rate);
//
//    }



    }
}









