package com.heiguang.portfolio.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI securityApiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("JWT Authentication and RBAC API")
                        .version("1.0.0")
                        .description("Spring Security portfolio API with access tokens, refresh rotation, and role-based authorization."))
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
