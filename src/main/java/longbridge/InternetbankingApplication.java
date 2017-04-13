package longbridge;

import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
public class InternetbankingApplication  implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(InternetbankingApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {


	}
}
