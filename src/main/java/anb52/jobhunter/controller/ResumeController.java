package anb52.jobhunter.controller;

import anb52.jobhunter.domain.Company;
import anb52.jobhunter.domain.Job;
import anb52.jobhunter.domain.Resume;
import anb52.jobhunter.domain.User;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.domain.response.resume.ResCreateResumeDTO;
import anb52.jobhunter.domain.response.resume.ResFetchResumeDTO;
import anb52.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import anb52.jobhunter.service.ResumeService;
import anb52.jobhunter.service.UserService;
import anb52.jobhunter.util.annotation.ApiMessage;
import anb52.jobhunter.util.error.IdInvalidException;
import anb52.jobhunter.util.error.SecurityUtil;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    @Autowired
    private final ResumeService resumeService;
    @Autowired
    private final UserService userService;

    private final FilterBuilder filterBuilder;

    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, UserService userService, FilterBuilder filterBuilder, FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume) throws IdInvalidException {
        boolean isValid = resumeService.checkResumeExistByUserAndJob(resume);
        if(!isValid){
            throw new IdInvalidException("User id/Job id không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchResumeById(resume.getId());
        if(resumeOptional.isEmpty()){
            throw new IdInvalidException("Resume với id = " + resume.getId() + "không tồn tại");
        }
        Resume reqResume = resumeOptional.get();
        reqResume.setStatus(resume.getStatus());

        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.update(reqResume));
    }


    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = this.resumeService.fetchResumeById(id);
        if(!resumeOptional.isPresent()){
            throw new IdInvalidException("Resume với id = " + id + "không tồn tại");
        }
        this.resumeService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Fetch a resume")
    public ResponseEntity<ResFetchResumeDTO> getResume(@PathVariable(name = "id") long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = resumeService.fetchResumeById(id);
        if(!resumeOptional.isPresent()){
            throw new IdInvalidException("Resume với id = " + id + "không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.convertToResFetchResumeDTO(resumeOptional.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> getResumeAll(
            @Filter Specification<Resume> spec,
            Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        // build specification
        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.fetchAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.fetchResumeByUser(pageable));
    }
}
