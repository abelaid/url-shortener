package ca.abelaid.url.shortener.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@ConfigurationProperties(prefix = "cors")
record CorsProperties(List<String> allowedOrigins) {

}
