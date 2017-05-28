package longbridge;

import longbridge.api.Finance;
import longbridge.models.FinancialInstitution;
import longbridge.models.FinancialInstitutionType;
import longbridge.repositories.*;
import longbridge.services.CorporateService;
import longbridge.services.IntegrationService;
import longbridge.services.MessageService;
import longbridge.services.OperationsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)

public class InternetbankingApplication  /*extends  SpringBootServletInitializer*/  {



    public static void main(String[] args) {
        //startup all jobs
        //   Timer timer = new Timer(1000 * 60 * 60 * 12, new DirectDebitJob());
        SpringApplication.run(InternetbankingApplication.class, args);


    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }


}

