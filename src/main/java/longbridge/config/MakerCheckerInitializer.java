//package longbridge.config;
//
//import longbridge.models.MakerChecker;
//import longbridge.repositories.MakerCheckerRepo;
//import longbridge.utils.Verifiable;
//
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
//import org.springframework.stereotype.Component;
//import java.lang.reflect.Method;
//
///**
// * Created by chiomarose on 19/06/2017.
// */
//
//@Component
//public class MakerCheckerInitializer implements InitializingBean{
//
//    @Autowired
//    MakerCheckerRepo makerCheckerRepo;
//
//    // private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
//        ClassPathScanningCandidateComponentProvider provider
//                = new ClassPathScanningCandidateComponentProvider(true);
//        return provider;
//    }
//
//
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		String packge = "longbridge.services.implementations";
//
//        ClassPathScanningCandidateComponentProvider provider = createComponentScanner();
//        for (BeanDefinition beanDef : provider.findCandidateComponents(packge)) {
//            System.out.println(beanDef.toString());
//            try {
//                Class<?> cl = Class.forName(beanDef.getBeanClassName());
//
//                Method[] methods = cl.getDeclaredMethods();
//
//                for (Method method : methods) {
//                    if (method.isAnnotationPresent(Verifiable.class)) {
//                        Verifiable verifyAnno = method.getAnnotation(Verifiable.class);
//                        String operation = verifyAnno.operation();
//                        String description = verifyAnno.description();
//
//                        if (!makerCheckerRepo.existsByOperation(operation)) {
//
//                            MakerChecker makerChecker = new MakerChecker();
//                            makerChecker.setVersion(0);
//                            makerChecker.setDelFlag("N");
//                            makerChecker.setOperation(operation);
//                            makerChecker.setDescription(description);
//                            makerChecker.setEnabled("N");
//                            System.out.print(makerChecker.toString());
//                            makerCheckerRepo.save(makerChecker);
//                        }
//                    }
//                }
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//	}
//
//
////    public static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
////        final List<Method> methods = new ArrayList<Method>();
////        Class<?> klass = type;
////        while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
////            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
////            final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
////            for (final Method method : allMethods) {
////                if (method.isAnnotationPresent(annotation)) {
////                    Annotation annotInstance = method.getAnnotation(annotation);
////                    // TODO process annotInstance
////                    methods.add(method);
////                }
////            }
////            // move to the upper class in the hierarchy in search for more methods
////            klass = klass.getSuperclass();
////        }
////        return methods;
////    }
//
//
//}
