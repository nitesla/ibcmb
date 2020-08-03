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
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by chiomarose on 19/06/2017.
 */

@Component
public class MakerCheckerInitializer implements InitializingBean {

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
        pathScan();
    }



    private void pathScan() {
        String pkg = "longbridge.services.implementations";

        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(Verifiable.class, false));

        provider.findCandidateComponents(pkg).stream().map(BeanDefinition::getBeanClassName)
                .map(this::getMethod).flatMap(Collection::stream).filter(m -> m.isAnnotationPresent(Verifiable.class))
                .map(this::create).forEach(this::checkAndCreate);
    }

    private void checkAndCreate(MakerChecker makerChecker) {
        if (!makerCheckerRepo.existsByOperation(makerChecker.getOperation())) {
            makerCheckerRepo.save(makerChecker);
            logger.info("Initialized {} ", makerChecker.toString());
        }
    }


    private Collection<Method> getMethod(String className) {

        try {
            Method[] declaredMethods = Class.forName(className).getDeclaredMethods();
            return List.of(declaredMethods);
        } catch (Exception e) {
            logger.warn("Skipping {} ,with {}", className, e.getMessage());
        }
        return Collections.emptySet();
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
