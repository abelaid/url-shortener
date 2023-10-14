package ca.abelaid.url.shortener;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Transactional
@Validated
@Service
class TrackingService {

    private final ShortenedUrlRepository shortenedUrlRepository;

    private final MeterRegistry meterRegistry;

    @Async
    void asyncClickCount(@NotEmpty String token, @URL String completeUrl) {
        shortenedUrlRepository.updateClickCount(token);
        Counter.builder("click_by_url")
                .tag("url", completeUrl)
                .tag("token", token)
                .register(meterRegistry)
                .increment();
    }
}
