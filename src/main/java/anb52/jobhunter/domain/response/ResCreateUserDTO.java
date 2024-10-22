package anb52.jobhunter.domain.response;

import anb52.jobhunter.util.constant.GenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
@Setter
@Getter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender; // Giới tính
    private String address;
    private Instant createdAt;
    private CompanyUser company;

    @Setter
    @Getter
    public static class CompanyUser{
        private long id;
        private String name;
    }
}
