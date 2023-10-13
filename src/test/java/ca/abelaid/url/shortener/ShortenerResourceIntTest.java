package ca.abelaid.url.shortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ShortenerResourceIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ShortenedUrlRepository shortenedUrlRepository;

    @Test
    void shouldShortenUrl() throws Exception {
        mvc.perform(get("/").param("url", "http://junit.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").exists());

    }

    @Test
    void shouldRedirectToCompleteUrl() throws Exception {
        shortenedUrlRepository.save(new ShortenedUrlEntity("toto", "http://junit.com"));

        mvc.perform(get("/toto"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("location", "http://junit.com"));

    }

    @Test
    void shouldNotRedirectToCompleteUrlWithUnknownToken() throws Exception {

        mvc.perform(get("/toto"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Unable to find shortened url with given token: toto"));

    }

    @Test
    void shouldGetToCompleteUrl() throws Exception {
        shortenedUrlRepository.save(new ShortenedUrlEntity("toto", "http://junit.com"));

        mvc.perform(get("/").param("shortened", "toto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("http://junit.com"));

    }


}
