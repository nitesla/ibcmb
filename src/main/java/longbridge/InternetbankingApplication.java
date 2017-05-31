package longbridge;

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

import java.util.List;
import java.util.Map;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)



public class InternetbankingApplication  extends SpringBootServletInitializer{

    @Autowired
    private SecurityService securityService;
    @Autowired
    private IntegrationService integrationService;

    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }



    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }
}





  




