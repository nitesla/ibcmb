package longbridge;


import longbridge.jobs.CompileJasper;
import longbridge.repositories.*;
import longbridge.services.SecurityService;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
@EnableBatchProcessing
@EnableScheduling
@EnableAsync
public class InternetbankingApplication extends SpringBootServletInitializer implements CommandLineRunner {
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
//        CompileJasper.compile();
//        securityService.unLockUs er("sunkoxy","Retail_Group");
//        securityService.unLockUser("fortune500","Coronation Group");

    }


}