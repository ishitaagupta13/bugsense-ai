package com.bugsense.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "commits")
@Data
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sha;

    private String message;
    private String authorName;
    private String authorEmail;
    private String authorLogin;
    private LocalDateTime committedAt;

    private Integer linesAdded;
    private Integer linesDeleted;
    private Integer filesChanged;

    private Double riskScore;
    private String riskLevel;
    private Boolean isBuggy;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;
}