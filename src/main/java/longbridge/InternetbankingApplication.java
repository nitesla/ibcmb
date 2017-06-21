package longbridge;

import longbridge.api.Rate;
import longbridge.config.makerchecker.MakerCheckerInitializer;
import longbridge.jobs.CronJobs;
import longbridge.models.MakerChecker;
import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.repositories.MakerCheckerRepo;
import longbridge.services.IntegrationService;

import longbridge.services.SecurityService;
import longbridge.utils.Verifiable;
import longbridge.utils.statement.AccountStatement;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)

public class InternetbankingApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private CronJobs cronJobs;
    @Autowired
    private MakerCheckerInitializer makerCheckerInitializer;


    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }

    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(true);
        return provider;
    }

    @Override
    public void run(String... strings) throws Exception {
//        cronJobs.startJob();
        makerCheckerInitializer.initialize();

    }
}










