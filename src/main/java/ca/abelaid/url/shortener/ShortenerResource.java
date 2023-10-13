package ca.abelaid.url.shortener;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
class ShortenerResource {

    private final ShortenerService shortenerService;

    @GetMapping(params = "url")
    ShortenerResponse shortenUrl(@Valid @NotNull @URL java.net.URL url) throws Exception {
        String shortenedUrl = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(shortenerService.shorten(url))
                .query(null)
                .encode()
                .build()
                .toUri()
                .toURL().toString();
        return new ShortenerResponse(shortenedUrl);
    }

    @GetMapping(params = "shortened")
    ShortenerResponse getCompleteUrl(@Valid String shortened) throws ShortenedUrlNotFoundException {
        return new ShortenerResponse(shortenerService.getCompleteUrl(shortened));
    }

    @GetMapping("/{shortened}")
    void redirectToCompleteUrl(@PathVariable String shortened, HttpServletResponse response) throws ShortenedUrlNotFoundException, IOException {
        response.sendRedirect(shortenerService.getCompleteUrl(shortened));
    }

}
