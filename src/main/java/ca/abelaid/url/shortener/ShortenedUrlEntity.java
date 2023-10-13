package ca.abelaid.url.shortener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SHORTENED_URL")
@Data
@AllArgsConstructor
@NoArgsConstructor
class ShortenedUrlEntity {
    @Id
    private String token;

    @Column(nullable = false, updatable = false)
    private String completeUrl;

}
