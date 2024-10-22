package anb52.jobhunter.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;


// config swagger
@Configuration
public class OpenAPIConfig {
    // yêu cầu xác thực cho API bằng Bearer Token
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    // Tạo các Server cho OpenAPI
    private Server createServer(String url, String description) {
        Server server = new Server();
        server.setUrl(url);
        server.setDescription(description);
        return server;
    }

    // Thong tin api
    private Info createApiInfo() {
        return new Info()
                .title("Job Hunter API")
                .version("1.0")
                .description("This API exposes all endpoints (job hunter)");
    }

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(List.of(
                        createServer("http://localhost:8080", "Server URL in Development environment")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
}