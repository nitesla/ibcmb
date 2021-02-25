//package longbridge.config;
//
//import longbridge.models.MakerChecker;
//import longbridge.models.Permission;
//import longbridge.repositories.MakerCheckerRepo;
//import longbridge.repositories.PermissionRepo;
//import longbridge.utils.Verifiable;
//import org.reflections.Reflections;
//import org.reflections.scanners.MethodAnnotationsScanner;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Method;
//import java.util.Set;
//
///**
// * Created by chiomarose on 19/06/2017.
// */
//
//@Component
//@ConditionalOnProperty(
//        value = "icon.boot",
//        havingValue = "true",
//        matchIfMissing = false)
//public class MakerCheckerInitializer implements InitializingBean {
//
//    @Autowired
//    MakerCheckerRepo makerCheckerRepo;
//
//    @Autowired
//    PermissionRepo permissionRepo;
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        pathScan();
//    }
//
//
//    private void pathScan() {
//        String pkg = "longbridge";
//
//        Set<Method> methodsAnnotatedWith = new Reflections(pkg, new MethodAnnotationsScanner()).getMethodsAnnotatedWith(Verifiable.class);
//        methodsAnnotatedWith.stream().map(this::create)
////                .peek(a -> System.out.println("@@@---" + a))
//                .forEach(this::checkAndCreate);
//    }
//
//    private void checkAndCreate(MakerChecker makerChecker) {
//        if (!makerCheckerRepo.existsByOperation(makerChecker.getOperation())) {
//            makerCheckerRepo.save(makerChecker);
//            logger.info("Initialized {} ", makerChecker.toString());
//        }
//
//        if(!permissionRepo.existsByNameAndUserType(makerChecker.getOperation() + "_V",makerChecker.getType().name())){
//            Permission p = new Permission();
//            p.setCategory("Verification");
//            p.setDescription("Verification for :" +makerChecker.getDescription());
//            p.setUserType(makerChecker.getType().name());
//            p.setCode(makerChecker.getOperation() + "_V");
//            p.setName("Verify:"+ (makerChecker.getDescription()));
//            permissionRepo.save(p);
//            logger.info("Added permission {} ", p);
//        }
//    }
//
//
//
//
//
//
//    private MakerChecker create(Method method) {
//        Verifiable annotation = method.getAnnotation(Verifiable.class);
//        String operation = annotation.operation();
//        String description = annotation.description();
//        MakerChecker makerChecker = new MakerChecker();
//        makerChecker.setVersion(0);
//        makerChecker.setDelFlag("N");
//        makerChecker.setOperation(operation);
//        makerChecker.setDescription(description);
//        makerChecker.setEnabled("N");
//        makerChecker.setType(annotation.type());
//        return makerChecker;
//    }
//
//}
