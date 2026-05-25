package com.bugsense.backend.service;

import com.bugsense.backend.model.Repository;
import com.bugsense.backend.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;

    public List<Repository> getAllRepositories() {
        return repositoryRepository.findAll();
    }

    public Repository saveRepository(Repository repo) {
        return repositoryRepository.save(repo);
    }

    public boolean exists(String fullName) {
        return repositoryRepository.existsByFullName(fullName);
    }
}