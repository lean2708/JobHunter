package anb52.jobhunter.repository;

import anb52.jobhunter.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume,Long>, JpaSpecificationExecutor<Resume> {
}
