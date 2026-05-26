package com.bugsense.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitHubRepoDTO {

    private Long id;
    private String name;
    private String description;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("default_branch")
    private String defaultBranch;

    @JsonProperty("stargazers_count")
    private Integer stars;

    private OwnerDTO owner;

    @Data
    public static class OwnerDTO {
        private String login;
    }
}