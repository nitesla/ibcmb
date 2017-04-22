package longbridge.models.audits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import longbridge.config.SpringContext;
import longbridge.repositories.AuditConfigRepo;

/**
 * Created by ayoade_farooq@yahoo.com on 4/19/2017.
 */
public class CustomJdbcUtil {
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public static boolean auditEntity(String s) {
//		ApplicationContext context = SpringContext.getApplicationContext();
//		AuditConfigRepo auditConfigRepo = context.getBean(AuditConfigRepo.class);
//		boolean ok = auditConfigRepo.existsByEntityNameAndEnabled(s,"Y");
		//TODO: Finish up audit
		return true;
	}

}
