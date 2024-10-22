package anb52.jobhunter.domain.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ResEmailJob {
    private String name;
    private double salary;
    private CompanyEmail company;
    private List<SkillEmail> skills;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class CompanyEmail{
        private String name;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class SkillEmail{
        private String name;
    }
}
