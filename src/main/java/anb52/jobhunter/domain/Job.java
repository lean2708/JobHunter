package anb52.jobhunter.domain;

import anb52.jobhunter.util.constant.LevelEnum;
import anb52.jobhunter.util.error.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Table(name = "jobs")
@Getter
@Setter
@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private LevelEnum level;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    private Instant startDate;
    private Instant endDate;
    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"jobs"})
    @JoinTable(name = "job_skill", joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Resume> resumes;

    @PrePersist
    public void hanldeBeforeCreate(){
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void hanldeBeforeUpdate(){
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }

}
