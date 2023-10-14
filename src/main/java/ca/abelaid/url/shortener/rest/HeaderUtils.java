package ca.abelaid.url.shortener.rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderUtils {
    public static HttpHeaders generatePaginationHttpHeaders(Page<?> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", Long.toString(page.getTotalElements()));
        headers.add("X-Page-Size", Integer.toString(page.getSize()));
        return headers;

    }
}
