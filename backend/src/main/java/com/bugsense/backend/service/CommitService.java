package com.bugsense.backend.service;

import com.bugsense.backend.dto.GitHubCommitDTO;
import com.bugsense.backend.model.Commit;
import com.bugsense.backend.model.Contributor;
import com.bugsense.backend.model.Repository;
import com.bugsense.backend.repository.CommitRepository;
import com.bugsense.backend.repository.ContributorRepository;
import com.bugsense.backend.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommitService {

    private final CommitRepository commitRepository;
    private final ContributorRepository contributorRepository;
    private final RepositoryRepository repositoryRepository;
    private final GitHubApiService gitHubApiService;

    public int fetchAndSaveCommits(String owner, String repoName, int perPage) {

        // 1. Find the repository in DB
        Repository repo = repositoryRepository
                .findByFullName(owner + "/" + repoName)
                .orElseThrow(() -> new RuntimeException("Repository not found. Add it first."));

        // 2. Fetch commits from GitHub API
        List<GitHubCommitDTO> githubCommits = gitHubApiService
                .getCommits(owner, repoName, perPage);

        int savedCount = 0;

        for (GitHubCommitDTO dto : githubCommits) {

            // Skip if already saved
            if (commitRepository.existsBySha(dto.getSha())) {
                continue;
            }

            // 3. Save contributor if not exists
            String authorLogin = dto.getAuthor() != null
                    ? dto.getAuthor().getLogin() : "unknown";

            contributorRepository.findByLogin(authorLogin)
                    .orElseGet(() -> {
                        Contributor c = new Contributor();
                        c.setLogin(authorLogin);
                        c.setTotalCommits(0);
                        c.setTotalBugsIntroduced(0);
                        c.setBugRate(0.0);
                        return contributorRepository.save(c);
                    });

            // 4. Build and save commit
            Commit commit = new Commit();
            commit.setSha(dto.getSha());
            commit.setMessage(dto.getCommit().getMessage());
            commit.setRepository(repo);
            commit.setAuthorLogin(authorLogin);

            if (dto.getCommit().getAuthor() != null) {
                commit.setAuthorName(dto.getCommit().getAuthor().getName());
                commit.setAuthorEmail(dto.getCommit().getAuthor().getEmail());

                String dateStr = dto.getCommit().getAuthor().getDate();
                if (dateStr != null) {
                    commit.setCommittedAt(LocalDateTime.parse(
                            dateStr, DateTimeFormatter.ISO_DATE_TIME));
                }
            }

            // Risk score will be set later by ML service
            commit.setRiskScore(0.0);
            commit.setRiskLevel("UNSCORED");
            commit.setIsBuggy(false);

            commitRepository.save(commit);
            savedCount++;
        }

        log.info("Saved {} new commits for {}/{}", savedCount, owner, repoName);
        return savedCount;
    }
}