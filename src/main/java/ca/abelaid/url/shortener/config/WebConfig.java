package ca.abelaid.url.shortener.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Optional;

@Configuration
@EnableWebMvc
@Slf4j
class WebConfig implements WebMvcConfigurer {
    @Autowired
    private CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration registration = registry.addMapping("/**")
                .allowedMethods("GET", "POST","PUT", "DELETE");;
        Optional.ofNullable(corsProperties.allowedOrigins()).ifPresent(allowedOrigins -> {
            allowedOrigins.forEach(allowedOrigin -> {
                registration.allowedOrigins(allowedOrigin);
                log.warn("Allowing origin {}", allowedOrigin);
            });
        });
    }
}
