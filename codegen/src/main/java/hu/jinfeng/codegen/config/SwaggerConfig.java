package hu.jinfeng.codegen.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket swagger2Config() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("hu.jinfeng"))
                .paths(PathSelectors.any())
                .build()
//                .securitySchemes(securitySchemes())
                ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("OpenAPI接口文档")
                .description("更多请咨询服务开发者")
                .version("0.1")
                .build();
    }

}
