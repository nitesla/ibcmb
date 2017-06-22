package longbridge.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
public class SpringContext implements ApplicationContextAware {

	private static  ApplicationContext context;
	
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		context = ctx;
	}


	public  static ApplicationContext getApplicationContext() {
		return context;
	}
	
}