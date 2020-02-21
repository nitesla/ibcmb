package longbridge.config;

import longbridge.utils.JasperReport.JasperReportsMultiFormatView;
import longbridge.utils.JasperReport.JasperReportsViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;
//import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;
import longbridge.utils.JasperReport.JasperReportsPdfView;

@Configuration
public class TemplateConfig {
	
	@Bean
	public JasperReportsViewResolver getJasperReportsViewResolver() {
	  JasperReportsViewResolver resolver = new JasperReportsViewResolver();
	  resolver.setPrefix("classpath:/jasperreports/");
	  resolver.setSuffix(".jasper");
	  resolver.setReportDataKey("datasource");
	  resolver.setViewNames("rpt_*");
	  resolver.setViewClass(JasperReportsMultiFormatView.class);
	  resolver.setOrder(0);
	  return resolver;
	}  

}
