package ca.abelaid.url.shortener;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "shortener")
record ShortenerProperties(@NotNull Integer randomMaxRetry, @NotNull Integer tokenMaxLength) {

}
