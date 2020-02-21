package longbridge.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

/**
 * Created by SYLVESTER on 6/18/2017.
 */

public class JasperReportsView {
    @Bean
    public ResourceBundleViewResolver getJasperReportsViewResolver() {
        ResourceBundleViewResolver resolver = new ResourceBundleViewResolver();
        resolver.setBasename("jasperreport-views");
        resolver.setOrder(0);
        return resolver;
    }

    @Bean
    public InternalResourceViewResolver getInternalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        resolver.setOrder(1);
        return resolver;
    }

//    @Bean
//    @Qualifier("helloWorldReport2")
//    public JasperReportsPdfView getHelloWorldReport() {
//        JasperReportsPdfView v = new JasperReportsPdfView();
//        v.setUrl("classpath:jrxmlPath");
//        v.setReportDataKey("datasource");
//        return v;
//    }
}