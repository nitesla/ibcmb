package longbridge.config;

import longbridge.security.adminuser.AdminAuthenticationSuccessHandler;
import longbridge.security.adminuser.AdminUserLoginInterceptor;
import longbridge.security.corpuser.CorporateUserLoginInterceptor;
import longbridge.security.opsuser.OpUserLoginInterceptor;
import longbridge.security.retailuser.RetailUserLoginInterceptor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Bean
    public CommonAnnotationBeanPostProcessor initCommonAnnotationBeanPostProcessor() {
        return new CommonAnnotationBeanPostProcessor();
    }

    @Bean()
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        builder.setConnectTimeout(1*60);
        builder.setReadTimeout(1*60);
        return builder.build();
    }


    //
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        final LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        registry.addInterceptor(localeChangeInterceptor);
        registry.addInterceptor(adminUserLoginInterceptor()).addPathPatterns("/admin/**");
        registry.addInterceptor(OpUserLoginInterceptor()).addPathPatterns("/ops/**");
        registry.addInterceptor(retailUserLoginInterceptor()).addPathPatterns("/retail/**");
        registry.addInterceptor(corporateUserLoginInterceptor()).addPathPatterns("/corporate/**");
        registry.addInterceptor(retailTransferAuthInterceptor()).addPathPatterns("/retail/transfer/process");
    }

    @Bean
    public LocaleResolver localeResolver() {
        final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
        cookieLocaleResolver.setDefaultLocale(Locale.ENGLISH);
        return cookieLocaleResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        String[] baseNames = new String[]{"i18n/messages", "i18n/menu"};
        source.setBasenames(baseNames);  // name of the resource bundle
        source.setCacheSeconds(1000);
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    //
    @Bean
    public AdminAuthenticationSuccessHandler successHandler() {
        AdminAuthenticationSuccessHandler handler = new AdminAuthenticationSuccessHandler();
        handler.setUseReferer(true);
        return handler;
    }

    @Bean
    public RetailTransferAuthInterceptor retailTransferAuthInterceptor() {
        return new RetailTransferAuthInterceptor();
    }

    @Bean
    public OpUserLoginInterceptor OpUserLoginInterceptor() {
        return new OpUserLoginInterceptor();
    }

    @Bean
    public AdminUserLoginInterceptor adminUserLoginInterceptor() {
        return new AdminUserLoginInterceptor();
    }

    @Bean
    public RetailUserLoginInterceptor retailUserLoginInterceptor() {
        return new RetailUserLoginInterceptor();
    }

    @Bean
    public CorporateUserLoginInterceptor corporateUserLoginInterceptor() {

        return new CorporateUserLoginInterceptor();
    }
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }


//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/resources/static/**")
//                .addResourceLocations("/resources/static/");
//    }
}