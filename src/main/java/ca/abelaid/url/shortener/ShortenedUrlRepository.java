package ca.abelaid.url.shortener;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

interface ShortenedUrlRepository extends JpaRepository<ShortenedUrlEntity, String> {

    Optional<ShortenedUrlEntity> findByCompleteUrl(String url);

    @Modifying
    @Query(value = "insert into ShortenedUrlEntity s (token, completeUrl) values (:token, :completeUrl)")
    void persist(@Param("token") String token, @Param("completeUrl") String completeUrl);

}
