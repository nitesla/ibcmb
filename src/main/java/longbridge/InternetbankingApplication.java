package longbridge;

import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.services.IntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
public class InternetbankingApplication implements CommandLineRunner {


	@Autowired
	IntegrationService service;

	public static void main(String[] args) {
		SpringApplication.run(InternetbankingApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception
	{
		System.out.println(service.viewAccountDetails("CC109"));
		System.out.println(service.isAccountValid("08166851634","yahoo.com","23-09-08"));
	}

}
