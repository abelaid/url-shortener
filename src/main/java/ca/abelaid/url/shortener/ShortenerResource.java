package ca.abelaid.url.shortener;

import ca.abelaid.url.shortener.rest.HeaderUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
class ShortenerResource {

    private final ShortenerService shortenerService;
    private final TrackingService trackingService;

    @GetMapping(params = "url", value = {"/", "/api/shorten"})
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
    void redirectToCompleteUrl(@PathVariable String shortened, HttpServletResponse response)
            throws ShortenedUrlNotFoundException, IOException {
        String completeUrl = shortenerService.getCompleteUrl(shortened);
        response.sendRedirect(completeUrl);
        trackingService.asyncClickCount(shortened, completeUrl);

    }

    @DeleteMapping("/api/shortened-urls/{shortened}")
    void deleteByToken(@PathVariable String shortened) {
        shortenerService.deleteByToken(shortened);
    }

    @GetMapping("/api/shortened-urls")
    ResponseEntity<List<ShortenedUrlDto>> getAll(Pageable pageable) {
        final Page<ShortenedUrlDto> page = shortenerService.getAll(pageable, ServletUriComponentsBuilder
                .fromCurrentRequest().replacePath("").toUriString());
        HttpHeaders headers = HeaderUtils.generatePaginationHttpHeaders(page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
