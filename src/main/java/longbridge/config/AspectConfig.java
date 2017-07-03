package longbridge.config;

//import longbridge.aop.AdminUserAdvisor;
import longbridge.aop.AdminUserAdvisor;
import longbridge.aop.MakerCheckerAdvisor;
import longbridge.aop.OpsUserAdvisor;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

/**
 * Created by Fortune on 6/28/2017.
 */
@Configuration
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
public class AspectConfig {

    @Bean
    public MakerCheckerAdvisor mkCheckerAspect(){

        MakerCheckerAdvisor aspect = Aspects.aspectOf(MakerCheckerAdvisor.class);
        return aspect;
    }

    @Bean
    public AdminUserAdvisor postAdminAspect(){

        AdminUserAdvisor aspect = Aspects.aspectOf(AdminUserAdvisor.class);
        return aspect;
    }

    @Bean
    public OpsUserAdvisor postOpsAspect(){

        OpsUserAdvisor aspect = Aspects.aspectOf(OpsUserAdvisor.class);
        return aspect;
    }
}
