package ca.abelaid.url.shortener;

interface ShortenedUrlMapper {

    static ShortenedUrlDto map(ShortenedUrlEntity entity, String baseUrl) {
        return new ShortenedUrlDto(entity.getToken(), baseUrl + "/" + entity.getToken(), entity.getCompleteUrl());
    }
}
