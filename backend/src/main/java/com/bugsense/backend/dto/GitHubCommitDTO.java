package com.bugsense.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitHubCommitDTO {

    private String sha;
    private CommitDetail commit;
    private AuthorDTO author;

    @Data
    public static class CommitDetail {
        private String message;
        private AuthorInfo author;

        @Data
        public static class AuthorInfo {
            private String name;
            private String email;
            private String date;
        }
    }

    @Data
    public static class AuthorDTO {
        private String login;
    }
}
