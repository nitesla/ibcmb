package longbridge;

import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.services.IntegrationService;
import longbridge.services.SecurityService;
import longbridge.utils.ImageWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;

import java.io.File;
import java.io.FileWriter;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)



public class InternetbankingApplication  extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private SecurityService securityService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private IntegrationService integrationService;
    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }

    @Override
   public void run(String... strings) throws Exception {

        ImageWriter image = new ImageWriter();
        String newImage = image.writeImage("/Users/Showboy/Documents/Longbridge/Projects/InternetBanking/ibcmb/src/main/resources/static/assets/phishing/dog.jpg", "wunmi");
        java.io.File img = new File("Sampleimage");
        java.io.FileWriter fw = new FileWriter(img);
        fw.write(newImage);
        fw.close();

//        System.out.println("Your password is "+passwordEncoder.encode("password123"));

    }
}










