package anb52.jobhunter.domain.response;

import anb52.jobhunter.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private int age;
    private GenderEnum gender; // Giới tính
    private String address;
    private Instant updateAt;
    private CompanyUser company;

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class CompanyUser{
        private long id;
        private String name;
    }
}