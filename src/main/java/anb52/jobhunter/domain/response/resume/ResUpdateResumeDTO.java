package anb52.jobhunter.domain.response.resume;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateResumeDTO {
    private Instant updateAt;
    private String updateBy;
}
