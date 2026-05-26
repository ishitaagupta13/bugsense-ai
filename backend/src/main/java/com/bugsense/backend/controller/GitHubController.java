package com.bugsense.backend.controller;

import com.bugsense.backend.dto.GitHubCommitDTO;
import com.bugsense.backend.dto.GitHubRepoDTO;
import com.bugsense.backend.service.GitHubApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/github")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubApiService gitHubApiService;

    @GetMapping("/repo/{owner}/{repoName}")
    public ResponseEntity<GitHubRepoDTO> getRepo(
            @PathVariable String owner,
            @PathVariable String repoName) {
        return ResponseEntity.ok(gitHubApiService.getRepository(owner, repoName));
    }

    @GetMapping("/repo/{owner}/{repoName}/commits")
    public ResponseEntity<List<GitHubCommitDTO>> getCommits(
            @PathVariable String owner,
            @PathVariable String repoName,
            @RequestParam(defaultValue = "10") int perPage) {
        return ResponseEntity.ok(gitHubApiService.getCommits(owner, repoName, perPage));
    }
}