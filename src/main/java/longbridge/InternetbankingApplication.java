package longbridge;

import longbridge.api.Rate;
import longbridge.jobs.CronJobs;
import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.services.BulkTransferService;
import longbridge.services.IntegrationService;

import longbridge.services.SecurityService;
import longbridge.utils.statement.AccountStatement;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
@EnableBatchProcessing

public class InternetbankingApplication /*extends SpringBootServletInitializer */implements CommandLineRunner {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    BulkTransferService service;
    @Autowired
    private CronJobs cronJobs;

    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }

    @Override
   public void run(String... strings) throws Exception {
        cronJobs.startJob();
        service.makeBulkTransferRequest("1169");


//      securityService.createEntrustUser("wumiTofu01","Wunmi baba ",true);
//        securityService.addUserContacts("soluwawunmi@gmail.com","07038810752",true,"wumiTofu01");
//         securityService.sendOtp("wumiTofu01");
//        System.out.println("Your password is "+passwordEncoder.encode("password123"));
//        integrationService.getAccountStatements("1234",new Date(),new Date());
//        System.out.println("end process");
    /*    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
       String dateInString = "7-Jun-2013";
        AccountStatement statement= integrationService.getAccountStatements("1990004170",formatter.parse(dateInString),null);
        System.out.println("<<<<<BEGIN>>>>>>");
        System.out.println(statement);
        System.out.println("<<<<END>>>>>>");*/
    }



//    @Override
//    public void run(String... strings) throws Exception {
//
//    Rate    rate=  integrationService.getFee("NIP");
//        System.out.println("Rate "+rate);
//
//    }


}










