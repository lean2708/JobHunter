package anb52.jobhunter.domain;

import anb52.jobhunter.util.constant.ResumeStateEnum;
import anb52.jobhunter.util.error.SecurityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Table(name = "resumes")
@Getter
@Setter
@Entity
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "email không được để trống")
    private String email;

    @NotBlank(message = "url không được để trống (upload cv chưa thành công)")
    private String url;

    @Enumerated(EnumType.STRING)
    private ResumeStateEnum status;

    private Instant createdAt;
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

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
