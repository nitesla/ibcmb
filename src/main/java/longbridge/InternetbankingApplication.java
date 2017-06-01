package longbridge;

import longbridge.api.AccountInfo;
import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.services.IntegrationService;
import longbridge.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.thymeleaf.TemplateEngine;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)



public class InternetbankingApplication  extends SpringBootServletInitializer implements CommandLineRunner{

    @Autowired
    private SecurityService securityService;
    @Autowired
    private TemplateEngine templateEngine;
   @Autowired
    private IntegrationService integrationService;

    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }



    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }


    @Override
    public void run(String... strings) throws Exception {

        Collection<AccountInfo> accounts = integrationService.fetchAccounts("R001959");
        for (AccountInfo acct : accounts) {
            System.out.print("Account recieved is: "+acct);
        }

    }
}










