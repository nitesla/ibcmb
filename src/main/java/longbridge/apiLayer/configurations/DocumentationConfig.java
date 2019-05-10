package longbridge.apiLayer.configurations;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;

import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class DocumentationConfig {


    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)

                .select()
                .apis(RequestHandlerSelectors.basePackage("longbridge.apiLayer"))
                .paths(regex("/api/.*"))
                .build()
                .apiInfo(metaData());
    }

    private ApiInfo metaData() {
        return new ApiInfo(
                "My REST API",
                "Some custom description of API.",
                "API TOS",
                "Terms of service",
                new Contact("John Doe", "www.example.com", "myeaddress@company.com"),
                "License of API", "API license URL", Collections.emptyList());
    }

//    @Bean
//    public Docket api() {
//        //Adding Header
//        ParameterBuilder aParameterBuilder = new ParameterBuilder();
//        aParameterBuilder.name("Authorization")                 // name of header
//                .modelRef(new ModelRef("string"))
//                .parameterType("header")               // type - header
//                .defaultValue("Bearer em9uZTpteXBhc3N3b3Jk")        // based64 of - zone:mypassword
//                .description("Enter the JWT generated when user logged in")
//                .required(true)                // for compulsory
//                .build();
//        java.util.List<Parameter> aParameters = new ArrayList<>();
//        aParameters.add(aParameterBuilder.build());             // add parameter
//        return new Docket(DocumentationType.SWAGGER_2).select()
//                .apis(RequestHandlerSelectors.basePackage("longbridge.apiLayer"))
//                .paths(regex("/api/.*"))
//                .build()
//                .pathMapping("")
//                .apiInfo(metaData())
//                .globalOperationParameters(aParameters);
//    }

}
