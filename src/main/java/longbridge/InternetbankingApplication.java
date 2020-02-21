package longbridge;


import longbridge.jobs.CompileJasper;
import longbridge.models.CorpPaymentRequest;
import longbridge.models.CustomDutyPayment;
import longbridge.models.CustomPaymentNotification;
import longbridge.models.CustomPaymentNotificationRequest;
import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.services.IntegrationService;
import longbridge.services.SecurityService;
import longbridge.utils.CustomDutyCode;
import longbridge.utils.EncryptionUtil;
import longbridge.utils.TransferType;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.math.BigDecimal;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
@EnableBatchProcessing
@EnableScheduling
@EnableAsync
public class InternetbankingApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private IntegrationService integrationService;
    @Autowired
    private SecurityService securityService;


    public static void main(String[] args) {
        SpringApplication.run(InternetbankingApplication.class, args);

    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InternetbankingApplication.class);
    }


    @Override
    public void run(String... strings) throws Exception {
//        String test45988test = EncryptionUtil.getSHA512("test"+"45988"+"test", null);
//        logger.info( "hash for receipt "+test45988test);
//        integrationService.reverseLocalTransfer("544627229");
//        System.out.println("code "+CustomDutyCode.getCustomDutyCodeByCode("00"));
//        integrationService.getReciept("45988");
    }

}