package longbridge.config;

import longbridge.models.MakerChecker;
import longbridge.models.Permission;
import longbridge.repositories.MakerCheckerRepo;
import longbridge.repositories.PermissionRepo;
import longbridge.trace.Trace;
import longbridge.utils.Verifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;




@Component
public class AnnotationInitializer implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MakerCheckerRepo makerCheckerRepo;
    @Autowired
    PermissionRepo permissionRepo;
    private Set<String> traceOps = new HashSet<>();

    private ClassPathScanningCandidateComponentProvider createComponentScanner() {
        ClassPathScanningCandidateComponentProvider provider
                = new ClassPathScanningCandidateComponentProvider(true);
        return provider;
    }

    public Set<String> getAllTraceOperations() {
        return traceOps;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String packge = "longbridge";

        ClassPathScanningCandidateComponentProvider provider = createComponentScanner();

        for (BeanDefinition beanDef : provider.findCandidateComponents(packge)) {
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
                            makerChecker.setOperation(verifyAnno.operation());
                            makerChecker.setDescription(verifyAnno.description());
                            makerChecker.setEnabled("N");
                            makerChecker.setType(verifyAnno.type());
                            makerChecker = makerCheckerRepo.save(makerChecker);
                            logger.debug("Initialized {} ", makerChecker);
                        }

                        if (!permissionRepo.existsByCodeAndUserType(verifyAnno.operation() + "_V", verifyAnno.type().name())) {
                            Permission p = new Permission();
                            p.setCategory("Verification");
                            p.setDescription("Verification for :" + verifyAnno.description());
                            p.setUserType(verifyAnno.type().name());
                            p.setCode(verifyAnno.operation() + "_V");
                            p.setName("Verify:" + (verifyAnno.description()));
                            permissionRepo.save(p);
                            logger.info("Added permission {} ", p);
                        }
                    }
                    if (method.isAnnotationPresent(Trace.class)) {
                        Trace trace = method.getAnnotation(Trace.class);
                        traceOps.addAll(Arrays.asList(trace.value()));
                    }
                }
            } catch (ClassNotFoundException e) {
                logger.error("Initialization Error", e);
            }
        }
    }

}
