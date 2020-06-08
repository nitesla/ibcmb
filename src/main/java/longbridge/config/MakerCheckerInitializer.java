package longbridge.config;

import longbridge.models.MakerChecker;
import longbridge.repositories.MakerCheckerRepo;
import longbridge.utils.Verifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by chiomarose on 19/06/2017.
 */

@Component
public class MakerCheckerInitializer implements InitializingBean{

    @Autowired
    MakerCheckerRepo makerCheckerRepo;

     private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(true);
        return provider;
    }



	@Override
	public void afterPropertiesSet() throws Exception {
		String packge = "longbridge.services.implementations";

        ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
        for (BeanDefinition beanDef : provider.findCandidateComponents(packge)) {
//            System.out.println(beanDef.toString());
            try {
                Class<?> cl = Class.forName(beanDef.getBeanClassName());

                Method[] methods = cl.getDeclaredMethods();

                for (Method method : methods) {
                    if (method.isAnnotationPresent(Verifiable.class)) {
                        Verifiable verifyAnno = method.getAnnotation(Verifiable.class);
                        String operation = verifyAnno.operation();
                        String description = verifyAnno.description();

                        if (!makerCheckerRepo.existsByOperation(operation)) {

                            MakerChecker makerChecker = new MakerChecker();
                            makerChecker.setVersion(0);
                            makerChecker.setDelFlag("N");
                            makerChecker.setOperation(operation);
                            makerChecker.setDescription(description);
                            makerChecker.setEnabled("N");
                            makerCheckerRepo.save(makerChecker);
                            logger.info("Initialized {} ",makerChecker.toString());
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
	}

}
