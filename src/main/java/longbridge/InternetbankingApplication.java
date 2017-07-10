package longbridge;

import longbridge.models.Account;
import longbridge.models.Code;
import longbridge.repositories.AccountRepo;
import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.services.IntegrationService;
import longbridge.services.SecurityService;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
@EnableBatchProcessing
@EnableAsync
public class InternetbankingApplication /*extends SpringBootServletInitializer */ implements CommandLineRunner {
    @Autowired
    AccountRepo repo;
    @Autowired
    IntegrationService service;


    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }


    @Override
    public void run(String... strings) throws Exception {

        repo.findAll()
                .stream()
                .forEach(
                        i ->
                        {
                            i.setCurrencyCode(service.viewAccountDetails(i.getAccountNumber()).getAcctCrncyCode());

                            repo.save(


                                    i

                            );

                        }
                );


    }
}










