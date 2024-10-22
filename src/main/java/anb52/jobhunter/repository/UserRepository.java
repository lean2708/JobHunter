package anb52.jobhunter.repository;


import anb52.jobhunter.domain.Company;
import anb52.jobhunter.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsById(long id);

    User findByRefreshTokenAndEmail(String token, String email);

    List<User> findByCompany(Company deleteCompany);
}
