package longbridge;

import longbridge.jobs.CronJobScheduler;
import longbridge.models.Account;
import longbridge.models.AccountRestriction;
import longbridge.models.OperationsUser;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.AccountRestrictionRepo;
import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.repositories.OperationsUserRepo;
import longbridge.services.CorporateService;
import longbridge.services.OperationsUserService;
import longbridge.services.PasswordPolicyService;
import longbridge.services.SecurityService;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.groovy.runtime.powerassert.SourceText;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
@EnableBatchProcessing
@EnableAsync
public class InternetbankingApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    SecurityService securityService;

    @Autowired
    AccountRestrictionRepo accountRepo;

    @Autowired
    OperationsUserRepo operationsUserRepo;

    @Autowired
    PasswordPolicyService passwordPolicyService;
    @Autowired
    CorporateService corporateService;


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
        System.out.println(corporateService.getCorporateByCorporateId("nwanu").getAccounts());


   }

}










