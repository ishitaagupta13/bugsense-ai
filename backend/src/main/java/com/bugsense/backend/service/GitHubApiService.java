package com.bugsense.backend.service;

import com.bugsense.backend.dto.GitHubCommitDTO;
import com.bugsense.backend.dto.GitHubRepoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubApiService {

    private final RestTemplate restTemplate;

    @Value("${github.api.base-url}")
    private String baseUrl;

    public GitHubRepoDTO getRepository(String owner, String repoName) {
        String url = baseUrl + "/repos/" + owner + "/" + repoName;
        log.info("Fetching repo: {}", url);
        return restTemplate.getForObject(url, GitHubRepoDTO.class);
    }

    public List<GitHubCommitDTO> getCommits(String owner, String repoName, int perPage) {
        String url = baseUrl + "/repos/" + owner + "/" + repoName
                + "/commits?per_page=" + perPage;
        log.info("Fetching commits: {}", url);
        GitHubCommitDTO[] commits = restTemplate.getForObject(url, GitHubCommitDTO[].class);
        return commits != null ? Arrays.asList(commits) : Collections.emptyList();
    }
}