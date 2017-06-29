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
//package longbridge.config;
//
//import java.lang.reflect.Method;
//
//import longbridge.models.MakerChecker;
//import longbridge.repositories.MakerCheckerRepo;
//import org.codehaus.groovy.runtime.powerassert.SourceText;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
//import org.springframework.stereotype.Component;
//
//import longbridge.utils.Verifiable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//
//
//@Component
//public class SpringClassScanner  implements InitializingBean{
//
//	@Autowired
//	MakerCheckerRepo makerCheckerRepo;
//
////	@Autowired
////	private EntityManager eman;
//
//
//   private final String path = "longbridge.services.implementations";
//
//
//    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
//        ClassPathScanningCandidateComponentProvider provider
//                = new ClassPathScanningCandidateComponentProvider(true);
//        return provider;
//    }
//
//	@Override
//	//@Transactional(readOnly = false,rollbackFor = Exception.class)
//	@Transactional(rollbackFor = Throwable.class)
//	public void afterPropertiesSet() throws Exception {
//		 ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
//	        for (BeanDefinition beanDef : provider.findCandidateComponents(path)) {
//	            System.out.println(beanDef.toString());
//	            Class<?> cl = Class.forName(beanDef.getBeanClassName());
//	            for (Method method : cl.getMethods())
//	                if( method.isAnnotationPresent(Verifiable.class)){
//	                	Verifiable[] verifiables = method.getAnnotationsByType(Verifiable.class);
//	                	for(Verifiable verifiable : verifiables){
//							//Verifiable verifiable = cl.getAnnotation(Verifiable.class);
//	                	String operation=verifiable.operation();
//	                	String description=verifiable.description();
//
//	                		if(!makerCheckerRepo.existsByCode(operation))
//							{
//								MakerChecker makerChecker=new MakerChecker();
//								makerChecker.setVersion(0);
//								makerChecker.setDelFlag("N");
//								makerChecker.setCode(operation);
//								makerChecker.setName(description);
//								makerChecker.setEnabled("N");
//
//								try{
//									makerCheckerRepo.save(makerChecker);
//								}
//								catch (Exception e)
//								{
//									e.printStackTrace();
//								}
//								//saveMakerChecker(makerChecker);
//								//eman.persist(makerChecker);
//							}
//
//	        	            System.out.println(String.format("The method %s is Verifiable, operation is %s and description is %s", cl.getName(),verifiable.operation(),verifiable.description()));
//
//
//
//
////	        	            if (!configRepo.existsByEntityName(verifiable.operation())) {
////	                            AuditConfig entity = new AuditConfig();
////	                            entity.setEnabled("N");
////	                            entity.setEntityName(e);
////	                            configRepo.save(entity);
////	                        }
//	                	}
//	                }
//			}
//	}
//
//
////	@Transactional
////	public void saveMakerChecker(MakerChecker makerChecker)
////	{
////	//	makerCheckerRepo.save(makerChecker);
////	}
//
//
//
//
//}
//

