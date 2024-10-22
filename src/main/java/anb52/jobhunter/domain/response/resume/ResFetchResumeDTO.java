package anb52.jobhunter.domain.response.resume;

import anb52.jobhunter.util.constant.ResumeStateEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResFetchResumeDTO {
    private long id;
    private String email;
    private String url;
    private ResumeStateEnum status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private String companyName;
    private UserResume user;
    private JobResume job;


    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResume{
        private long id;
        private String name;
    }

    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobResume{
        private long id;
        private String name;
    }
}
