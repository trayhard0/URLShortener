package io.github.trayhard0.urlshortener.repository;

import io.github.trayhard0.urlshortener.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByCode(String code);
}
