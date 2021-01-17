package longbridge.trace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

@Configuration
public class TraceConfig {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean(destroyMethod = "destroy")
    public ThreadLocalTargetSource threadLocalTraceStore() {
        log.info("In threadLocalTraceStore");
        ThreadLocalTargetSource result = new ThreadLocalTargetSource();
        result.setTargetBeanName("traceStore");
        return result;
    }

    @Primary
    @Bean(name = "proxiedThreadLocalTargetSource")
    public ProxyFactoryBean proxiedThreadLocalTargetSource(ThreadLocalTargetSource threadLocalTargetSource) {
        log.info("In proxiedThreadLocalTargetSource");
        ProxyFactoryBean result = new ProxyFactoryBean();
        result.setTargetSource(threadLocalTargetSource);
        return result;
    }

    @Bean(name = "traceStore")
    @Scope(scopeName = "prototype")
    public TraceStore tenantStore() {
        log.trace("In TraceStore tenantStore()");
        return new TraceStore();
    }

//    @Bean
//    public Filter traceFilter() {
//        return new TraceFilter();
//    }
}
