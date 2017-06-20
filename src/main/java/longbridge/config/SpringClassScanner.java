package longbridge.config;

import java.lang.reflect.Method;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.stereotype.Component;

import longbridge.utils.Verifiable;

@Component
public class SpringClassScanner  implements InitializingBean{
 
   private final String path = "longbridge.services.implementations";
 
 
    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false);
        return provider;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		 ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
	        for (BeanDefinition beanDef : provider.findCandidateComponents(path)) {
	            System.out.println(beanDef.toString());
	            Class<?> cl = Class.forName(beanDef.getBeanClassName());
	            for (Method method : cl.getMethods())
	                if( method.isAnnotationPresent(Verifiable.class)){
	                	Verifiable[] verifiables = method.getAnnotationsByType(Verifiable.class);
	                	for(Verifiable verifiable : verifiables){
	                		//Verifiable verifiable = cl.getAnnotation(Verifiable.class);
	        	            System.out.println(String.format("The method %s is Verifiable, operation is %s and description is %s", cl.getName(),verifiable.operation(),verifiable.description()));
	        	    
//	        	            if (!configRepo.existsByEntityName(verifiable.operation())) {
//	                            AuditConfig entity = new AuditConfig();
//	                            entity.setEnabled("N");
//	                            entity.setEntityName(e);
//	                            configRepo.save(entity);
//	                        }
	                	}
	                }
	                     
	                }
	}
 
}

