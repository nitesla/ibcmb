package longbridge;

import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.repositories.CorporateRepo;
import longbridge.repositories.CustomJpaRepositoryFactoryBean;
import longbridge.repositories.PermissionRepo;
import longbridge.repositories.RoleRepo;
import longbridge.services.CorporateService;
import longbridge.services.IntegrationService;
import longbridge.services.MessageService;
import longbridge.services.OperationsUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
public class InternetbankingApplication/*extends SpringBootServletInitializer*/  {

    @Autowired
    OperationsUserService operationsUserService;
    @Autowired
    MessageService messageService;


    @Autowired
    CorporateService corporateService;
    @Autowired
    IntegrationService integrationService;

    @Autowired
    CorporateRepo corporateRepo;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    PermissionRepo permissionRepo;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //	@Autowired
    //	IntegrationService service;

    public static void main(String[] args) {
        //startup all jobs
     //   Timer timer = new Timer(1000 * 60 * 60 * 12, new DirectDebitJob());
        SpringApplication.run(InternetbankingApplication.class, args);


 }




//	@Override
//	@Transactional
//	public void run(String... strings) throws Exception {
////		OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
//		MailBox mailBox= messageService.getMailBox(opsUser.getId(), UserType.OPERATIONS);
//		Iterable<Message> sent = messageService.getSentMessages(mailBox);
//		logger.info("Mailbox is {}",sent);


}
