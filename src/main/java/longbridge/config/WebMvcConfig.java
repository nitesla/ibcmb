package longbridge.config;

import longbridge.security.adminuser.AdminAuthenticationSuccessHandler;
import longbridge.security.adminuser.AdminUserLoginInterceptor;
import longbridge.security.corpuser.CorporateUserLoginInterceptor;
import longbridge.security.opsuser.OpUserLoginInterceptor;
import longbridge.security.retailuser.RetailUserLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Autowired
    private RetailTransferAuthInterceptor retailTransferAuthInterceptor;
    @Autowired
    private AdminUserLoginInterceptor adminUserLoginInterceptor;
    @Autowired
    RetailUserLoginInterceptor retailUserLoginInterceptor;
    @Autowired
    private CorporateUserLoginInterceptor corporateUserLoginInterceptor;
    @Autowired
    private OpUserLoginInterceptor opUserLoginInterceptor;



    //
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
        registry.addInterceptor(adminUserLoginInterceptor).addPathPatterns("/admin/**");
        registry.addInterceptor(opUserLoginInterceptor).addPathPatterns("/ops/**");
        registry.addInterceptor(retailUserLoginInterceptor).addPathPatterns("/retail/**");
        registry.addInterceptor(corporateUserLoginInterceptor).addPathPatterns("/corporate/**");
        registry.addInterceptor(retailTransferAuthInterceptor).addPathPatterns("/retail/transfer/process");
        registry.addInterceptor(corpTransferAuthInterceptor()).addPathPatterns("/corporate/transfer/process");
        registry.addInterceptor(webContentInterceptor()).addPathPatterns("/retail/**");
    }


    //
    @Bean
    public AdminAuthenticationSuccessHandler successHandler() {
        return new AdminAuthenticationSuccessHandler();
    }

    @Bean
    public CorporateTransferAuthInterceptor corpTransferAuthInterceptor() {
        return new CorporateTransferAuthInterceptor();
    }


    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    @Bean
    public WebContentInterceptor webContentInterceptor() {
        WebContentInterceptor interceptor = new WebContentInterceptor();
        interceptor.setCacheSeconds(-1);
        //Removing deprecated methods
        //TODO: Remove comments after observation 9-Feb-2021
//        interceptor.setUseExpiresHeader(true);
//        interceptor.setAlwaysMustRevalidate(true);
//        interceptor.setUseCacheControlHeader(true);
//        interceptor.setUseCacheControlNoStore(true);
        return interceptor;
    }




}