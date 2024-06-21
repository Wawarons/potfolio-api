package me.podsialdy.api.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Utils.EmailContent;

@Service
@Slf4j
public class EmailService {

    @Value("${spring.mail.username}")
    private String hostEmail;

    private JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    /**
     * Sends a validation code to the specified customer via email.
     * 
     * @param customer the customer to whom the validation code will be sent
     * @param code     the validation code to be included in the email message
     */
    public void sendValidationCode(Customer customer, String code) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            String messageContent = String.format(EmailContent.VALIDATION_CODE_MESSAGE, code);

            helper.setFrom(hostEmail);
            helper.setTo(customer.getEmail());
            helper.setSubject(EmailContent.VALIDATION_CODE_SUBJECT);
            helper.setText(messageContent, true);

            javaMailSender.send(message);
            log.info("Code validation send to customer {}", customer.getId());

        } catch (MessagingException mex) {
            log.error("Error sending validation code to customer {}", customer.getId());
            mex.printStackTrace();
        }
    }

}
