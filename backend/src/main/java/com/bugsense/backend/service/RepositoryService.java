package com.bugsense.backend.service;

import com.bugsense.backend.model.Repository;
import com.bugsense.backend.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import com.bugsense.backend.dto.GitHubRepoDTO;
import com.bugsense.backend.service.GitHubApiService;
@Service
@RequiredArgsConstructor
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;
    private final GitHubApiService gitHubApiService;
    public List<Repository> getAllRepositories() {
        return repositoryRepository.findAll();
    }

    public Repository saveRepository(Repository repo) {
        return repositoryRepository.save(repo);
    }

    public boolean exists(String fullName) {
        return repositoryRepository.existsByFullName(fullName);
    }
    public Repository fetchAndSaveRepository(String owner, String repoName) {

        // Check if already exists
        String fullName = owner + "/" + repoName;
        if (repositoryRepository.existsByFullName(fullName)) {
            return repositoryRepository.findByFullName(fullName).get();
        }

        // Fetch from GitHub
        GitHubRepoDTO dto = gitHubApiService.getRepository(owner, repoName);

        // Save to DB
        Repository repo = new Repository();
        repo.setName(dto.getName());
        repo.setOwner(dto.getOwner().getLogin());
        repo.setFullName(dto.getFullName());
        repo.setDescription(dto.getDescription());
        repo.setDefaultBranch(dto.getDefaultBranch());
        repo.setStars(dto.getStars());

        return repositoryRepository.save(repo);
    }

}
