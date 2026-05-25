package com.bugsense.backend.repository;

import com.bugsense.backend.model.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@org.springframework.stereotype.Repository
public interface RepositoryRepository extends JpaRepository<Repository, Long> {
    Optional<Repository> findByFullName(String fullName);
    boolean existsByFullName(String fullName);
}