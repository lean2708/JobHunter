package anb52.jobhunter.domain;

import anb52.jobhunter.util.error.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Table(name = "subscribers")
@Getter
@Setter
@Entity
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank(message = "name không được để trống")
    private String name;
    @NotBlank(message = "email không được để trống")
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "subscribers" })
    @JoinTable(name = "subscriber_skill", joinColumns = @JoinColumn(name =
            "subscriber_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

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
