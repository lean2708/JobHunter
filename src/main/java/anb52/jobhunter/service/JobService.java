package anb52.jobhunter.service;

import anb52.jobhunter.domain.Company;
import anb52.jobhunter.domain.Job;
import anb52.jobhunter.domain.Skill;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.domain.response.job.ResCreateJobDTO;
import anb52.jobhunter.domain.response.job.ResUpdateJobDTO;
import anb52.jobhunter.repository.CompanyRepository;
import anb52.jobhunter.repository.JobRepository;
import anb52.jobhunter.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    @Autowired
    private final JobRepository jobRepository;
    @Autowired
    private final SkillRepository skillRepository;
    @Autowired
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }


    public ResCreateJobDTO create(Job job) {
        // check skills
        if(job.getSkills() != null){
            List<Long> reqSkills = new ArrayList<>();
            // lay danh sach skill_id
            for (Skill skill : job.getSkills()) {
                reqSkills.add(skill.getId()); // lay tung id
            }
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills); // lay danh sach skill tu danh sach skill_id
            job.setSkills(dbSkills);
        }

        if(job.getCompany()!= null){
            Optional<Company> companyOptional = this.companyRepository.findById(job.getCompany().getId());
            if (companyOptional.isPresent()){
                job.setCompany(companyOptional.get());
            }
        }

        // create job
        Job currentJob = this.jobRepository.save(job);

        // convert response
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = new ArrayList<>();
            for (Skill skill : currentJob.getSkills()) {
                skills.add(skill.getName());
            }
            dto.setSkills(skills);
        }

        return dto;
    }



    public ResUpdateJobDTO update(Job job, Job jobDB) {
        // check skills
        if(job.getSkills() != null){
            List<Long> reqSkills = new ArrayList<>();
            for (Skill skill : job.getSkills()) { // lay skill job gui len
                reqSkills.add(skill.getId()); // lay tung id
            }
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            // Cap nhap vao Job DB
            jobDB.setSkills(dbSkills);
        }

        // check company
        if(job.getCompany() != null){
            Optional<Company> companyOptional = this.companyRepository.findById(job.getCompany().getId());
            if (companyOptional.isPresent()){
                jobDB.setCompany(companyOptional.get());
            }
        }

        // update thong tin vao Job DB
        jobDB.setName(job.getName());
        jobDB.setSalary(job.getSalary());
        jobDB.setQuantity(job.getQuantity());
        jobDB.setLocation(job.getLocation());
        jobDB.setLevel(job.getLevel());
        jobDB.setStartDate(job.getStartDate());
        jobDB.setEndDate(job.getEndDate());
        jobDB.setActive(job.isActive());

        //update job
        Job currentJob = this.jobRepository.save(jobDB);

        // convert response
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = new ArrayList<>();
            for (Skill skill : currentJob.getSkills()) {
                skills.add(skill.getName());
            }
            dto.setSkills(skills);
        }

        return dto;
    }

    public Optional<Job> fetchJobById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageUser = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);

        rs.setResult(pageUser.getContent());

        return rs;
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }
    public boolean isIdExist(Job job){
        if(this.jobRepository.existsById(job.getId())){
            return true;
        }
        return false;
    }
}
