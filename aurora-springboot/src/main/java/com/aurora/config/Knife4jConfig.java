package com.aurora.config;


import com.aurora.service.SystemConfigProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfig {

    @Autowired
    private SystemConfigProviderService configProvider;

    @Bean
    public Docket createRestApi() {
        String termsOfServiceUrl = configProvider.getConfig("swagger.termsOfServiceUrl", "https://www.gysy.ltd");
        
        return new Docket(DocumentationType.SWAGGER_2)
                .protocols(Collections.singleton("https"))
                .host(termsOfServiceUrl)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.aurora.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        String contactName = configProvider.getConfig("swagger.contact.name", "admin");
        String contactEmail = configProvider.getConfig("swagger.contact.email", "");
        String termsOfServiceUrl = configProvider.getConfig("swagger.termsOfServiceUrl", "https://www.gysy.ltd");
        String termsOfServiceUrlapi = configProvider.getConfig("swagger.termsOfServiceUrlapi", "https://www.gysy.ltd/api/");
        
        return new ApiInfoBuilder()
                .title("aurora文档")
                .description("aurora")
                .contact(new Contact(contactName, termsOfServiceUrl, contactEmail))
                .termsOfServiceUrl(termsOfServiceUrlapi)
                .version("1.0")
                .build();
    }

}
