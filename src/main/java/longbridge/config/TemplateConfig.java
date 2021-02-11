package longbridge.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;


@Configuration
public class TemplateConfig {

//    @Bean
//    public JasperReportsViewResolver getJasperReportsViewResolver() {
//        JasperReportsViewResolver resolver = new JasperReportsViewResolver();
//        resolver.setPrefix("classpath:/jasperreports/");
//        resolver.setSuffix(".jasper");
//        resolver.setReportDataKey("datasource");
//        resolver.setViewNames("rpt_*");
//        resolver.setViewClass(JasperReportsMultiFormatView.class);
//        resolver.setOrder(0);
//        return resolver;
//    }


    @Bean("msgTemplate")
    SpringResourceTemplateResolver msgTemplateResolver(ApplicationContext appCtx) {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(appCtx);
        templateResolver.setPrefix("classpath:/messaging/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

}
