package anb52.jobhunter.controller;

import anb52.jobhunter.domain.Job;
import anb52.jobhunter.domain.Skill;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.domain.response.job.ResCreateJobDTO;
import anb52.jobhunter.domain.response.job.ResUpdateJobDTO;
import anb52.jobhunter.service.JobService;
import anb52.jobhunter.util.annotation.ApiMessage;
import anb52.jobhunter.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    @Autowired
    private final JobService jobService;


    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(job.getId());
        if(!currentJob.isPresent()){
            throw new IdInvalidException("Job not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.update(job,currentJob.get()));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job by id")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }

        return ResponseEntity.ok().body(currentJob.get());
    }

    @GetMapping("/jobs")
    @ApiMessage("Get job with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec,
            Pageable pageable) {

        return ResponseEntity.ok().body(this.jobService.fetchAll(spec, pageable));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.delete(id);
        return ResponseEntity.ok().body(null);
    }
}
