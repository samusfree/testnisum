package com.nisum.testnisum.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info().title("User API").description("User API for Nisum Challenge")
                        .contact(new Contact().email("sagonzales89@gmail.com"))
                        .license(new License().name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                        .version("1.0.0"))
                .tags(List.of(
                        new Tag().name("user").description("User")
                ))
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT").in(SecurityScheme.In.HEADER).name("Authorization")));
    }
}
