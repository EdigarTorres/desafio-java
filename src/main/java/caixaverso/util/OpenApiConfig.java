package caixaverso.util;

import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                description = "API para gerenciamento de produtos de empréstimos",
                title = "Caixaverso API",
                version = "1.0"
        ))

public class OpenApiConfig extends Application {
}
