package longbridge;

import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.services.IntegrationService;
import longbridge.services.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)


public class InternetbankingApplication  /*extends  SpringBootServletInitializer*/ implements CommandLineRunner {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private IntegrationService integrationService;

    public static void main(String[] args) {
        //startup all jobs
        //   Timer timer = new Timer(1000 * 60 * 60 * 12, new DirectDebitJob());
        SpringApplication.run(InternetbankingApplication.class, args);


    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }


    @Override
    public void run(String... strings) throws Exception {
        System.out.println("start of call");
//        securityService.createEntrustUser("bridger09","longbridger",true);
//        integrationService.sendSMS("HELLO FROM FAROOQ", "08023972141", "HELLO");
//        integrationService.sendSMS("HELLO FROM FAROOQ", "08052237245", "HELLO");
//        integrationService.sendSMS("HELLO FROM FAROOQ", "08052090487", "HELLO");
//        java.util.List<String> questions = new ArrayList<>();
//        questions.add("WHO IS THE GREATEST");
//        List<String> answer = new ArrayList<>();
//        answer.add("AYAOADE FAROOQ IS ");
//        securityService.setUserQA("bridger09", questions, answer);
//        System.out.println("end of call");


    }

}

