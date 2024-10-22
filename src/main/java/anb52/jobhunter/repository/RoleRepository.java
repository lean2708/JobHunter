package anb52.jobhunter.repository;

import anb52.jobhunter.domain.Resume;
import anb52.jobhunter.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>, JpaSpecificationExecutor<Role> {
    boolean existsByName(String name);

    Role findByName(String name);
}
