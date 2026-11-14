package io.github.trayhard0.urlshortener;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "links")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String originalUrl;
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 64)
    private String manageTokenHash;
    private Instant createdAt;
    private Instant expiresAt;

}
