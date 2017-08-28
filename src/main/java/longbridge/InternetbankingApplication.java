package longbridge;

import longbridge.jobs.CronJobScheduler;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.repositories.OperationsUserRepo;
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
    CronJobService cronJobService;

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

        CronJobScheduler.startJobs();

    }

}










