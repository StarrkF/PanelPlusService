package com.example.panelplus.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("PanelPlus API Yönetimi")
                        .version("1.0")
                        .description("Çok dilli menü ve post yönetim sistemi API dökümantasyonu"))
                // Tüm endpoint'ler için bu güvenlik şemasını zorunlu kıl
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        // Bearer Token (JWT) yapısını Swagger'a tanımlıyoruz
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Lütfen JWT token'ınızı buraya girin (Başına 'Bearer' yazmayın, otomatik eklenir).")));
    }
}
