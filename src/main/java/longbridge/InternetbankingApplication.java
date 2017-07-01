package longbridge;

import longbridge.config.MakerCheckerInitializer;
import longbridge.jobs.CronJobScheduler;
import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
@EnableBatchProcessing

public class InternetbankingApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private MakerCheckerInitializer makerCheckerInitializer;

    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }


    @Override
    public void run(String... strings) throws Exception {
      //  makerCheckerInitializer.initialize();
    }
}










