package anb52.jobhunter.service;

import anb52.jobhunter.domain.Job;
import anb52.jobhunter.repository.JobRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class EmailService {

    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JobRepository jobRepository;

    public EmailService(MailSender mailSender, JavaMailSender javaMailSender, SpringTemplateEngine templateEngine, JobRepository jobRepository) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.jobRepository = jobRepository;
    }

    //Gửi một email đơn giản chứa văn bản (không có HTML hoặc tệp đính kèm)
    public void sendSimpleEmail(){
        SimpleMailMessage msg = new SimpleMailMessage(); // đối tượng đơn giản của Spring, được dùng khi bạn không cần xử lý HTML
        msg.setTo("lean2k4@gmail.com"); // nguoi nhan
        msg.setSubject("Testing from Spring Boot");  // tieu de
        msg.setText("Hello World from Spring Boot Email");  // noi dung
        this.mailSender.send(msg);
    }

    // Gửi email đồng bộ với khả năng hỗ trợ HTML
    public void sendEmailSync(String to, String subject, String content,
                              boolean isMultipart, boolean isHtml) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();  // thành phần cốt lõi để xử lý email với nội dung phức tạp
        try {
            MimeMessageHelper message = new MimeMessageHelper(
                    mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            this.javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }

    // Gửi email đồng bộ với nội dung lấy từ một template (HTML)
    @Async
    public void sendEmailFromTemplateSync(String to, String subject,
                                          String templateName,String username, Object value) {
        Context context = new Context();
        context.setVariable("name", username);
        context.setVariable("jobs", value);

        String content = this.templateEngine.process(templateName, context); // convert tu html sang string
        this.sendEmailSync(to, subject, content, false, true);
    }
}
