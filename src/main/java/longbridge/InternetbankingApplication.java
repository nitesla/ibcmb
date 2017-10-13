package longbridge;


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


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
@EnableBatchProcessing
@EnableAsync
public class InternetbankingApplication extends SpringBootServletInitializer implements CommandLineRunner {

<<<<<<< HEAD

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

    @Autowired
    CronJobService cronJobService;

    @Autowired
    BulkTransferRepo bulkTransferRepo;

    @Autowired
    FinancialInstitutionService financialInstitutionService;


=======
>>>>>>> 7c820936e5c0d03b8a2ffc82da427961368786a3
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