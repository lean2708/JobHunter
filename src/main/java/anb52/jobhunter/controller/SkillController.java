package anb52.jobhunter.controller;

import anb52.jobhunter.domain.Skill;
import anb52.jobhunter.domain.User;
import anb52.jobhunter.domain.response.ResCreateUserDTO;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.service.SkillService;
import anb52.jobhunter.util.annotation.ApiMessage;
import anb52.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    @Autowired
    private final SkillService skillService;
    public SkillController(SkillService skillService){
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("create a skill")
    public ResponseEntity<Skill> create(@Valid @RequestBody Skill s) throws IdInvalidException {
        // check name
       if(s.getName() != null && skillService.isNameExitst(s.getName())){
           throw new IdInvalidException("Skill name = " + s.getName() + " đã tồn tại");
       }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(s));
    }

    @PutMapping("/skills")
    @ApiMessage("update a skill")
    public ResponseEntity<Skill> update(@Valid @RequestBody Skill s) throws IdInvalidException {
        // check id
        Skill curentSkill = this.skillService.fetchSkillById(s.getId());
        if(curentSkill == null){
            throw new IdInvalidException("Skill id = " + s.getId() + " đã tồn tại");
        }
        // check name
        if(s.getName() != null && skillService.isNameExitst(s.getName())){
            throw new IdInvalidException("Skill name = " + s.getName() + " đã tồn tại");
        }

        curentSkill.setName(s.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.updateSkill(curentSkill));
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getAll(
            @Filter Specification spec, Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(
                this.skillService.fetchAllSkills(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        // check id
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if(currentSkill == null){
            throw new IdInvalidException("Skill id = " + id + " đã tồn tại");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
