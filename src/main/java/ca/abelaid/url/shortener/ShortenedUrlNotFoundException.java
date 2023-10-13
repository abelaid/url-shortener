package ca.abelaid.url.shortener;

class ShortenedUrlNotFoundException extends Exception {
    ShortenedUrlNotFoundException(String token) {
        super("Unable to find shortened url with given token: " + token);
    }
}
