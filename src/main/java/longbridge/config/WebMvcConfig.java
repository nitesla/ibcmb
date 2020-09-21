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
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {

    }

    @Override
    public void addFormatters(FormatterRegistry registry) {

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {

    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {

    }

    @Override
    public Validator getValidator() {
        return null;
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

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
//    @Bean
//    @Primary
//    @ConfigurationProperties(prefix = "app.datasource")
//    public DataSource primarySource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.secondDatasource")
//    public DataSource secondarySource() {
//        return DataSourceBuilder.create().build();
//    }


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
        //  registry.addInterceptor(webContentInterceptor()).addPathPatterns("/retail/**");
        // registry.addInterceptor(webContentInterceptor()).addPathPatterns("/retail/**");
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
        interceptor.setUseExpiresHeader(true);
        interceptor.setAlwaysMustRevalidate(true);
        interceptor.setUseCacheControlHeader(true);
        interceptor.setUseCacheControlNoStore(true);
        return interceptor;
    }



//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/resources/static/**")
//                .addResourceLocations("/resources/static/");
//    }


}