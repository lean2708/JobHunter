package anb52.jobhunter.service;

import anb52.jobhunter.domain.Job;
import anb52.jobhunter.domain.Resume;
import anb52.jobhunter.domain.User;
import anb52.jobhunter.domain.response.ResUserDTO;
import anb52.jobhunter.domain.response.ResultPaginationDTO;
import anb52.jobhunter.domain.response.resume.ResCreateResumeDTO;
import anb52.jobhunter.domain.response.resume.ResFetchResumeDTO;
import anb52.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import anb52.jobhunter.repository.JobRepository;
import anb52.jobhunter.repository.ResumeRepository;
import anb52.jobhunter.repository.UserRepository;
import anb52.jobhunter.util.error.IdInvalidException;
import anb52.jobhunter.util.error.SecurityUtil;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    @Autowired
    private final ResumeRepository resumeRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final JobRepository jobRepository;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public ResCreateResumeDTO create(Resume resume) throws IdInvalidException {

        this.resumeRepository.save(resume);

        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());

        return res;
    }

    public boolean checkResumeExistByUserAndJob(Resume resume){
        if(resume.getUser() == null || resume.getJob() == null){
            return false;
        }
        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if(userOptional.isEmpty() || jobOptional.isEmpty()){
            return false;
        }
        return true;
    }

    public Optional<Resume> fetchResumeById(long id){
        Optional<Resume> resume = this.resumeRepository.findById(id);
        if(resume.isPresent()){
            return resume;
        }
        return null;
    }

    public ResultPaginationDTO fetchAllResume(Specification<Resume> spec, Pageable pageable){
        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);
        List<Resume> listResume = pageResume.getContent();

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber()+ 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        List<ResFetchResumeDTO> listResResume = new ArrayList<>();
        for(Resume resume : listResume){
            listResResume.add(convertToResFetchResumeDTO(resume));
        }
        rs.setResult(listResResume);

        return rs;
    }

    public ResUpdateResumeDTO update(Resume resume){
        resume = this.resumeRepository.save(resume);

        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdateAt(resume.getUpdatedAt());
        res.setUpdateBy(resume.getUpdatedBy());
        return res;
    }
    public void delete(long id){
        this.resumeRepository.deleteById(id);
    }
    public ResFetchResumeDTO convertToResFetchResumeDTO(Resume resume){
        ResFetchResumeDTO res = new ResFetchResumeDTO();

        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedBy(resume.getUpdatedBy());

        if(resume.getJob() != null){ // Nếu có job phù hợp cv thì update company name vào
            res.setCompanyName(resume.getJob().getCompany().getName());
        }

        ResFetchResumeDTO.UserResume userResume = new ResFetchResumeDTO.UserResume();
        userResume.setId(resume.getUser().getId());
        userResume.setName(resume.getUser().getName());
        res.setUser(userResume);

        ResFetchResumeDTO.JobResume jobResume = new ResFetchResumeDTO.JobResume();
        jobResume.setId(resume.getJob().getId());
        jobResume.setName(resume.getJob().getName());
        res.setJob(jobResume);

        return res;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get() : "";
        // Tạo bộ lọc dữ liệu
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);

        Page<Resume> pageResume = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageResume.getTotalPages());
        mt.setTotal(pageResume.getTotalElements());

        rs.setMeta(mt);

        // remove sensitive data
        List<ResFetchResumeDTO> listResume = new ArrayList<>();
        for(Resume resume : pageResume.getContent()){
            listResume.add(convertToResFetchResumeDTO(resume));
        }

        rs.setResult(listResume);

        return rs;
    }

}
