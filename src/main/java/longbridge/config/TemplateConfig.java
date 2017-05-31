package longbridge.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LongBridge on 2/8/2017.
 */
@Configuration
@EnableWebMvc
public class TemplateConfig {



    @Bean
    ITemplateResolver xmlTemplateResolver(ApplicationContext appCtx) {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();

        templateResolver.setApplicationContext(appCtx);
        templateResolver.setPrefix("classpath:/templates/xml/");
        templateResolver.setSuffix(".xml");
        templateResolver.setOrder(2);
        templateResolver.setTemplateMode("XML");
        templateResolver.setCharacterEncoding(UTF_8.name());
        templateResolver.setCacheable(false);

        return templateResolver;
    }


    @Bean
    ITemplateResolver htmlTemplateResolver(ApplicationContext appCtx) {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();

        templateResolver.setApplicationContext(appCtx);
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setOrder(1);
        templateResolver.setCharacterEncoding(UTF_8.name());
        templateResolver.setCacheable(false);


        return templateResolver;
    }


    @Bean
    TemplateEngine templateEngine(ApplicationContext appCtx)
    {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(xmlTemplateResolver(appCtx));
        templateEngine.addTemplateResolver(htmlTemplateResolver(appCtx));
        return templateEngine;
    }




//    @Bean
//    public ViewResolver viewResolver(ApplicationContext appCtx) {
//        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
//        resolver.setTemplateEngine(templateEngine(appCtx));
//        resolver.setCharacterEncoding("UTF-8");
//        return resolver;
//    }
}
