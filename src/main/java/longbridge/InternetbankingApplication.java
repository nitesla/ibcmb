package longbridge;


import longbridge.api.Rate;

import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
public class InternetbankingApplication /*extends SpringBootServletInitializer implements CommandLineRunner*/ {


    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }


//    @Override
//    public void run(String... strings) throws Exception {
//
//    Rate    rate=  integrationService.getFee("NIP");
//        System.out.println("Rate "+rate);
//
//    }


}










