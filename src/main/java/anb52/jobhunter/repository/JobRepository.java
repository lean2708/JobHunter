package anb52.jobhunter.repository;

import anb52.jobhunter.domain.Job;
import anb52.jobhunter.domain.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    boolean existsById(long id);

    List<Job> findBySkillsIn(List<Skill> skills);
}
