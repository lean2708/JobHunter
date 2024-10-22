package anb52.jobhunter.domain;

import anb52.jobhunter.util.error.SecurityUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Table(name = "companies")
@Getter
@Setter
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     long id;
    @NotBlank(message = "name không được để trống")
     String name;
    @Column(columnDefinition = "MEDIUMTEXT") // Để lưu trữ lớn hơn 256 kí tự
     String description;
 String address;
     String logo;

     Instant createdAt;
     Instant updatedAt;
     String createdBy;
     String updatedBy;

    @OneToMany( mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    List<User> users;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    List<Job> jobs;

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
