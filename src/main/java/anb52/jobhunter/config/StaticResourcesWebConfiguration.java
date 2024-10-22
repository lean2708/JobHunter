package anb52.jobhunter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {
    // cấu hình đường dẫn tĩnh cho các tệp được tải lên
    @Value("${anb52.upload-file.base-uri}")
    private String baseURI;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/storage/**") // định nghĩa đường dẫn URL
                .addResourceLocations(baseURI); // vị trí tìm kiếm tài nguyên
    }
}