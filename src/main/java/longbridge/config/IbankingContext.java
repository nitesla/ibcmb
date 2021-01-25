package longbridge.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Component;


@Component
public class IbankingContext implements ApplicationContextAware {

	private static ApplicationContext context;

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		synchronized (this) {
			if (IbankingContext.context == null) {
				IbankingContext.context = applicationContext;
			}
		}
	}

	public static <T> T getBean(Class<T> clazz) {
		if (context == null)
			throw new ApplicationContextException("application context not initialized");
		return context.getBean(clazz);
	}

	public static <T> T getBean(String qualifier, Class<T> clazz) {
		return context.getBean(qualifier, clazz);
	}

}