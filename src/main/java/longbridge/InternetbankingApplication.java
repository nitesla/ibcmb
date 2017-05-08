package longbridge;

import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.services.MessageService;
import longbridge.services.OperationsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.transaction.Transactional;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
public class InternetbankingApplication implements CommandLineRunner {

	@Autowired
	OperationsUserService operationsUserService;
	@Autowired
	MessageService messageService;
	Logger logger = LoggerFactory.getLogger(this.getClass());

	//	@Autowired
	//	IntegrationService service;

	public static void main(String[] args) {
		SpringApplication.run(InternetbankingApplication.class, args);
	}

	@Override
	@Transactional
	public void run(String... strings) throws Exception {
//		OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
//		MailBox mailBox= messageService.getMailBox(opsUser.getId(), UserType.OPERATIONS);
//		Iterable<Message> sent = messageService.getSentMessages(mailBox);
//		logger.info("Mailbox is {}",sent);
	}

}
