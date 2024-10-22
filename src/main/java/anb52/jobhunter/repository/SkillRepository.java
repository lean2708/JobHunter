package anb52.jobhunter.repository;

import anb52.jobhunter.domain.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {
    boolean existsByName(String name);

    // DS cac skill co id nam trong DS ID truyen vao
    List<Skill> findByIdIn(List<Long> listId);
}
