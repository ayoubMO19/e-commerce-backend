package com.vexa.ecommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    tags = {
            @Tag(name = "Auth", description = "Autenticación y gestión de sesión"),
            @Tag(name = "Users", description = "Gestión del perfil de usuario"),
            @Tag(name = "Products", description = "Consulta de productos"),
            @Tag(name = "Orders", description = "Gestión de pedidos"),
            @Tag(name = "Cart", description = "Carrito de compra"),
            @Tag(name = "Payments", description = "Pagos con Stripe"),
            @Tag(name = "Admin", description = "Operaciones administrativas")
    }
)
public class SwaggerConfig {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().components(new Components().addSecuritySchemes
                        ("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("VEXA E-COMMERCE REST API")
                        .description("This is the official VEXA Ecommerce REST API.")
                        .version("1.0").contact(new Contact().name("ayoubMO19")
                                .email( "ayoubmorghiouhda@gmail.com").url("https://github.com/ayoubMO19"))
                        .license(new License().name("Apache 2.0")
                                .url("https://www.apache.org")));
    }
}
