package longbridge.config;

import longbridge.aop.*;
import org.aspectj.lang.Aspects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.EnableLoadTimeWeaving;

/**
 * Created by Fortune on 6/28/2017.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
public class AspectConfig {

    @Bean
    public MakerCheckerAdvisor mkCheckerAspect(){

        return Aspects.aspectOf(MakerCheckerAdvisor.class);
    }

    @Bean
    public AdminUserAdvisor postAdminAspect(){

        return Aspects.aspectOf(AdminUserAdvisor.class);
    }

    @Bean
    public OpsUserAdvisor postOpsAspect(){

        return Aspects.aspectOf(OpsUserAdvisor.class);
    }

    @Bean
    public RetailUserAdvisor postRetailAspect(){

        return Aspects.aspectOf(RetailUserAdvisor.class);
    }

    @Bean
    public CorporateUserAdvisor postCorporateUserAspect(){

        return Aspects.aspectOf(CorporateUserAdvisor.class);
    }

    @Bean
    public CorporateAdvisor postCorporateAspect(){

        return Aspects.aspectOf(CorporateAdvisor.class);
    }
}
