package com.bugsense.backend.repository;

import com.bugsense.backend.model.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {
    boolean existsBySha(String sha);
    Optional<Commit> findBySha(String sha);
}