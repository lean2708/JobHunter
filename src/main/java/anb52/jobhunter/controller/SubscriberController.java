package anb52.jobhunter.controller;

import anb52.jobhunter.domain.Subscriber;
import anb52.jobhunter.service.SubscriberService;
import anb52.jobhunter.util.annotation.ApiMessage;
import anb52.jobhunter.util.error.IdInvalidException;
import anb52.jobhunter.util.error.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber sb) throws IdInvalidException {
        // check email
        boolean isExist = this.subscriberService.isExistsByEmail(sb.getEmail());
        if (isExist == true){
            throw new IdInvalidException("Email " + sb.getEmail() + " đã tồn tại.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(sb));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get() : "";
        return ResponseEntity.status(HttpStatus.OK).body(this.subscriberService.findByEmail(email));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a supscriber")
    public ResponseEntity<Subscriber> update(@RequestBody Subscriber subsRequest) throws IdInvalidException {
        // check id
        Subscriber subsDB = this.subscriberService.findById(subsRequest.getId());
        if (subsDB == null){
            throw new IdInvalidException("Id " + subsRequest.getId() + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.subscriberService.update(subsDB, subsRequest));
    }


}
