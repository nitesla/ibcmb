package longbridge.config.makerchecker;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import longbridge.models.MakerChecker;
import longbridge.repositories.MakerCheckerRepo;
import longbridge.utils.Verifiable;
import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by chiomarose on 19/06/2017.
 */

@Component
public class MakerCheckerInitializer {

    @Autowired
    MakerCheckerRepo makerCheckerRepo;
   // private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void initializeMakerChecker() {
        Reflections reflections = new Reflections("longbridge.services.implementations");


        Set<Class<? extends Object>> clazzes =reflections.getTypesAnnotatedWith(Verifiable.class,true);
        
        for (Class aClass : clazzes) {

            Method[] methods = aClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(Verifiable.class))
                {
                    Verifiable verifyAnno = method.getAnnotation(Verifiable.class);
                    String operation = verifyAnno.operation();
                    String description = verifyAnno.description();

                    if(!makerCheckerRepo.existsByCode(operation))
                    {

                        MakerChecker makerChecker=new MakerChecker();
                        makerChecker.setCode(operation);
                        makerChecker.setName(description);
                        makerChecker.setEnabled("N");
                        makerChecker.setVersion(0);
                        makerChecker.setDelFlag("N");
                        System.out.print(makerChecker.toString());
                        makerCheckerRepo.save(makerChecker);
                    }
                }
            }
        }
    }


//    public static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
//        final List<Method> methods = new ArrayList<Method>();
//        Class<?> klass = type;
//        while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
//            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
//            final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
//            for (final Method method : allMethods) {
//                if (method.isAnnotationPresent(annotation)) {
//                    Annotation annotInstance = method.getAnnotation(annotation);
//                    // TODO process annotInstance
//                    methods.add(method);
//                }
//            }
//            // move to the upper class in the hierarchy in search for more methods
//            klass = klass.getSuperclass();
//        }
//        return methods;
//    }


}
