package anb52.jobhunter.service;

import anb52.jobhunter.domain.Job;
import anb52.jobhunter.domain.Skill;
import anb52.jobhunter.domain.Subscriber;
import anb52.jobhunter.domain.email.ResEmailJob;
import anb52.jobhunter.repository.JobRepository;
import anb52.jobhunter.repository.SkillRepository;
import anb52.jobhunter.repository.SubscriberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository, JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public boolean isExistsByEmail(String email){
        return subscriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber sb){
        // check skill
        if(sb.getSkills() != null){
            List<Long> reqSkills = sb.getSkills().stream().
                    map(x->x.getId()).collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            sb.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(sb);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> subscriberOptional = this.subscriberRepository.findById(id);
        if (subscriberOptional!= null){
            return subscriberOptional.get();
        }
        return null;
    }

    public Subscriber update(Subscriber subsDB, Subscriber subsRequest) {
// check skill
        if(subsRequest.getSkills() != null){
            List<Long> reqSkills = subsRequest.getSkills().stream().
                    map(x->x.getId()).collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subsDB);
    }


    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().
                map(skill -> new ResEmailJob.SkillEmail(skill.getName())).
                collect(Collectors.toList());
        res.setSkills(s);

        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
                         List<ResEmailJob> arr = listJobs.stream().map(
                         job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }
}
