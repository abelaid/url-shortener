package ca.abelaid.url.shortener;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.net.URL;

@RequiredArgsConstructor
@Service
@Validated
@Slf4j
class ShortenerService {

    private final ShortenedUrlRepository shortenedUrlRepository;
    private final ShortenerProperties shortenerProperties;

    @Transactional
    String shorten(@NotNull URL url) {
        // FIXME should sanitize
        String completeUrl = url.toString();
        return shortenedUrlRepository
                .findByCompleteUrl(completeUrl)
                .map(ShortenedUrlEntity::getToken)
                .orElseGet(() -> createShortenedUrl(completeUrl, 0));
    }

    @Transactional(readOnly = true)
    String getCompleteUrl(@NotEmpty String token) throws ShortenedUrlNotFoundException {
        return shortenedUrlRepository.findById(token)
                .orElseThrow(() -> new ShortenedUrlNotFoundException(token)).getCompleteUrl();
    }

    // private  methods
    private String createShortenedUrl(String url, int retry) {
        String token = RandomStringUtils.randomAlphanumeric(1, shortenerProperties.tokenMaxLength());
        try {
            shortenedUrlRepository.persist(token, url);
        } catch (DataIntegrityViolationException e) {
            if (retry == shortenerProperties.randomMaxRetry()) {
                log.error("Max attempt reached", e);
                return null;
            } else {
                log.warn("Retrying because of collision");
                token = createShortenedUrl(url, retry + 1);
            }
        }
        return token;
    }

}
