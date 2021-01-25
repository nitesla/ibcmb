package longbridge.trace;

import longbridge.config.IbankingContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class TraceMonitor {


    @Pointcut("within(longbridge..*)")
    public void inProject() {
    }

    @Pointcut("@annotation(operation)")
    public void isTraced(Trace operation) {
    }


    @Before("inProject()  && isTraced(op)")
    public void advice(JoinPoint jp, Trace op) {
        ApplicationContext context = IbankingContext.getApplicationContext();
        context.getBean(TraceStore.class).add(op.value());
    }


}
