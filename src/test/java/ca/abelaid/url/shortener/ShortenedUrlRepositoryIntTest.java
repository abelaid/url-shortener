package ca.abelaid.url.shortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class ShortenedUrlRepositoryIntTest {

    @Autowired
    private ShortenedUrlRepository tested;

    @Test
    void shouldPersist() {
        tested.persist("1", "http://junit");
    }

    @Test
    void shouldNotPersistTheSameToken() {
        tested.persist("1", "http://junit");
        assertThatThrownBy(() -> tested.persist("1", "http://another.junit"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
