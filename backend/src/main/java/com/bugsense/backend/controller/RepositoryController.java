package com.bugsense.backend.controller;

import com.bugsense.backend.model.Repository;
import com.bugsense.backend.service.CommitService;
import com.bugsense.backend.service.RepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repositories")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService repositoryService;
    private final CommitService commitService;

    @GetMapping("/health")
    public String health() {
        return "BugSense Backend is running!";
    }

    @GetMapping
    public ResponseEntity<List<Repository>> getAllRepositories() {
        return ResponseEntity.ok(repositoryService.getAllRepositories());
    }

    // Add a repo and fetch its commits in one shot
    @PostMapping("/connect")
    public ResponseEntity<Map<String, Object>> connectRepository(
            @RequestParam String owner,
            @RequestParam String repoName,
            @RequestParam(defaultValue = "50") int commitCount) {

        Repository repo = repositoryService.fetchAndSaveRepository(owner, repoName);
        int saved = commitService.fetchAndSaveCommits(owner, repoName, commitCount);

        return ResponseEntity.ok(Map.of(
                "repository", repo.getFullName(),
                "commitsSaved", saved,
                "message", "Repository connected successfully"
        ));
    }
}