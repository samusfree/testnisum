package com.nisum.testnisum.configuration;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi tweetsOpenApi(@Value("${springdoc.api.version}") String appVersion) {
        String[] paths = { "/api/v1/users/**" };
        return GroupedOpenApi.builder().
                group("users")
                .addOpenApiCustomizer(openApi -> openApi.info(new Info().title("Tweets API").version(appVersion)))
                .pathsToMatch(paths)
                .build();
    }
}
