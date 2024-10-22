package anb52.jobhunter.service;

import anb52.jobhunter.domain.Job;
import anb52.jobhunter.domain.Skill;
import anb52.jobhunter.domain.User;
import anb52.jobhunter.domain.response.ResUserDTO;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
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
public class SkillService {
    @Autowired
    private SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository){
        this.skillRepository = skillRepository;
    }

    public boolean isNameExitst(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if(skillOptional.isPresent()){
            return skillOptional.get();
        }
        return null;
    }
    public Skill createSkill(Skill s) {
        return this.skillRepository.save(s);
    }


    public Skill updateSkill(Skill curentSkill) {
        return this.skillRepository.save(curentSkill);
    }

    public ResultPaginationDTO fetchAllSkills(Specification spec, Pageable pageable) {
        Page<Skill> pageSkills = this.skillRepository.findAll(spec, pageable);
        List<Skill> listSkills = pageSkills.getContent();

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber()+ 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageSkills.getTotalPages());
        mt.setTotal(pageSkills.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(listSkills);

        return rs;
    }

    public void deleteSkill(long id) {
        // delete data job_skill (van con job)
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        List<Job> listJob = currentSkill.getJobs();
        for(Job job : listJob){
            job.getSkills().remove(currentSkill);
        }

        // delete subscriber (inside subscriber_skill table)
        currentSkill.getSubscribers().forEach(subs->subs.getSkills().remove(currentSkill));

        // delte skill
        this.skillRepository.delete(currentSkill);
    }
}
