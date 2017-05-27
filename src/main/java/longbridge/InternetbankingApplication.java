package longbridge;

import com.fasterxml.jackson.databind.node.ObjectNode;
import longbridge.models.Role;
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
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.stream.Collectors;


@SpringBootApplication
@EnableJpaRepositories(repositoryFactoryBeanClass = CustomJpaRepositoryFactoryBean.class)
public class InternetbankingApplication  /*extends  SpringBootServletInitializer*/ implements CommandLineRunner  {

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

    @SpringBootApplication
    public class SpringBootWebApplication extends SpringBootServletInitializer {

        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
            return application.sources(InternetbankingApplication.class);
        }
    }

    public static void main(String[] args) {
        //startup all jobs
     //   Timer timer = new Timer(1000 * 60 * 60 * 12, new DirectDebitJob());
        SpringApplication.run(InternetbankingApplication.class, args);


 }




    @Override
    public void run(String... strings) throws Exception {
        Role role = roleRepo.findOne(2L);
        role.setPermissions(
                permissionRepo.findAll()
                        .stream()
                        .filter(i -> i.getUserType().equals("RETAIL"))
                        .collect(Collectors.toList())


        );
        roleRepo.save(role);
    }



//	@Override
//	@Transactional
//	public void run(String... strings) throws Exception {
////		OperationsUserDTO opsUser = operationsUserService.getUser(1L);//TODO get current user
//		MailBox mailBox= messageService.getMailBox(opsUser.getId(), UserType.OPERATIONS);
//		Iterable<Message> sent = messageService.getSentMessages(mailBox);
//		logger.info("Mailbox is {}",sent);


}
