package com.toolkit.hub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j (Swagger) configuration
 *
 * @author zhangna
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Toolkit Hub API Documentation")
                        .version("1.0.0")
                        .description("Personal toolkit collection website API")
                        .contact(new Contact()
                                .name("zhangna")
                                .email("your-email@example.com")));
    }
}
