package longbridge.config;

import longbridge.models.MakerChecker;
import longbridge.repositories.MakerCheckerRepo;
import longbridge.utils.Verifiable;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by chiomarose on 19/06/2017.
 */

@Component
public class MakerCheckerInitializer implements InitializingBean {

    @Autowired
    MakerCheckerRepo makerCheckerRepo;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public void afterPropertiesSet() throws Exception {
        pathScan();
    }


    private void pathScan() {
        String pkg = "longbridge.services";

        Set<Method> methodsAnnotatedWith = new Reflections(pkg, new MethodAnnotationsScanner()).getMethodsAnnotatedWith(Verifiable.class);
        methodsAnnotatedWith.stream().map(this::create).
        peek(a -> System.out.println("@@@---" + a)).forEach(this::checkAndCreate);
    }

    private void checkAndCreate(MakerChecker makerChecker) {
        if (!makerCheckerRepo.existsByOperation(makerChecker.getOperation())) {
            makerCheckerRepo.save(makerChecker);
            logger.info("Initialized {} ", makerChecker.toString());
        }
    }




    private MakerChecker create(Method method) {
        Verifiable annotation = method.getAnnotation(Verifiable.class);
        String operation = annotation.operation();
        String description = annotation.description();
        MakerChecker makerChecker = new MakerChecker();
        makerChecker.setVersion(0);
        makerChecker.setDelFlag("N");
        makerChecker.setOperation(operation);
        makerChecker.setDescription(description);
        makerChecker.setEnabled("N");
        return makerChecker;
    }

}
