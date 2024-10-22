package anb52.jobhunter.domain.response;

import anb52.jobhunter.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender; // Giới tính
    private String address;
    private Instant updateAt;
    private Instant createdAt;

    private CompanyUser company;

    private RoleUser role;

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class CompanyUser{
        private long id;
        private String name;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class RoleUser{
        private long id;
        private String name;
    }
}