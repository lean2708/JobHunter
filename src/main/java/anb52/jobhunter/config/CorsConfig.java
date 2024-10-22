package anb52.jobhunter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// Cau hinh CORS
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins( // Cho phep cac URL nao co the ket noi backend
                Arrays.asList("http://localhost:3000","http://localhost:4173","http://localhost:5173"));
        // Các HTTP method được phép ket noi
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Cac header duoc gui len
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept", "x-no-retry"));
        // Gui kem cookie hay khong
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new
                UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cấu hình này cho tất cả các đường dẫn
        return source;
    }
}
