package longbridge.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import longbridge.utils.Verifiable;

@Component
public class SpringClassScanner  implements InitializingBean{
 
   private final String path = "longbridge.services.implementations";
 
 
    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        // Don't pull default filters (@Component, etc.):
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Verifiable.class));
        return provider;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		 ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
	        for (BeanDefinition beanDef : provider.findCandidateComponents(path)) {
	            System.out.println(beanDef.toString());
	            Class<?> cl = Class.forName(beanDef.getBeanClassName());
	            Verifiable verifiable = cl.getAnnotation(Verifiable.class);
	            System.out.println(String.format("The method %s is Verifiable, operation is %s and description is %s", cl.getName(),verifiable.operation(),verifiable.description()));
	        }
	}
 
//    private void printMetadata(BeanDefinition beanDef) {
//        try {
//            Class<?> cl = Class.forName(beanDef.getBeanClassName());
//            Findable findable = cl.getAnnotation(Findable.class);
//            System.out.printf("Found class: %s, with meta name: %s%n",
//                    cl.getSimpleName(), findable.name());
//        } catch (Exception e) {
//            System.err.println("Got exception: " + e.getMessage());
//        }
//    }
}

