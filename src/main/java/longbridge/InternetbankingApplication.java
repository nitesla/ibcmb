package longbridge;

import longbridge.jobs.CronJobScheduler;
import longbridge.models.AdminUser;
import longbridge.models.CorporateUser;
import longbridge.models.OperationsUser;
import longbridge.models.RetailUser;
import longbridge.repositories.*;
import longbridge.services.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
@EnableBatchProcessing
@EnableAsync
public class InternetbankingApplication extends SpringBootServletInitializer implements CommandLineRunner {


    @Autowired
    OperationsUserRepo operationsUserRepo;

    @Autowired
    PasswordPolicyService passwordPolicyService;
    @Autowired
    IntegrationService integrationService;


    @Autowired
    AccountRepo accountRepo;

    @Autowired
    AccountService accountService;

    @Autowired
    CorporateService corporateService;


    @Autowired
    CronJobService cronJobService;

    @Autowired
    CorporateRepo corporateRepo;
    @Autowired
    TransferService transferService;

    @Autowired
    RetailUserRepo retailUserService;

    @Autowired
    CorporateUserRepo corporateUserService;

    @Autowired
    AdminUserRepo adminUserService;

    @Autowired
    OperationsUserRepo operationsUserService;

    @Autowired
    SecurityService securityService;


    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);

    }


    @Override
    public void run(String... strings) throws Exception {

    }


}










