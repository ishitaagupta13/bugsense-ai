package com.bugsense.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "contributors")
@Data
public class Contributor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    private Integer totalCommits;
    private Integer totalBugsIntroduced;
    private Double bugRate;
}
