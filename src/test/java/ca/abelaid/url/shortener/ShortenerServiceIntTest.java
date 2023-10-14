package ca.abelaid.url.shortener;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class ShortenerServiceIntTest {

    @Autowired
    private ShortenerService tested;

    @Autowired
    private ShortenerProperties shortenerProperties;

    @Autowired
    private ShortenedUrlRepository shortenedUrlRepository;

    @Test
    void shouldNotShortenNullUrl() {
        assertThatThrownBy(() -> tested.shorten(null))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldShortenUrl() throws Exception {
        String token = tested.shorten(URI.create("http://junit").toURL());
        assertThat(token).isNotNull();
        assertThat(token.length()).isBetween(1, shortenerProperties.tokenMaxLength());
    }

    @Test
    void shouldNotGetCompleteUrlWithNullToken() {
        assertThatThrownBy(() -> tested.getCompleteUrl(null))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldNotGetCompleteUrlWithEmptyToken() {
        assertThatThrownBy(() -> tested.getCompleteUrl(""))
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    void shouldGetCompleteUrl() throws Exception {
        shortenedUrlRepository.save(ShortenedUrlEntity.builder().token("fakeTOKEN").completeUrl("http://junit").build());

        String completeUrl = tested.getCompleteUrl("fakeTOKEN");

        assertThat(completeUrl).isEqualTo("http://junit");

    }
}
