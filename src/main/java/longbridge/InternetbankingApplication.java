package longbridge;

import longbridge.dtos.CorporateUserDTO;
import longbridge.jobs.CronJobScheduler;
import longbridge.models.Account;
import longbridge.models.Corporate;
import longbridge.models.CorporateUser;
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

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
@EnableBatchProcessing
@EnableAsync
public class InternetbankingApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    SecurityService securityService;


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
    CorporateUserRepo userRepo;

    @Autowired
    CorporateUserService corporateUserService;

    @Autowired
    CorporateRepo corporateRepo;

    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }


    @Override
    @Transactional
    public void run(String... strings) throws Exception {




//        CronJobScheduler.startJobs();

    }

}










