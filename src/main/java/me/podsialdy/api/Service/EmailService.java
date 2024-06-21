package me.podsialdy.api.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Entity.Customer;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendValidationCode(Customer customer, String code) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            String messageContent = "<html><body>"
                    + "<h2 style='color: #fcb974;'>Votre code de validation</h2>"
                    + "<p>Une tentative de connexion à votre compte a été détectée.</p>"
                    + "<p>Utilisez le code suivant pour valider votre connexion :</p>"
                    + "<h3 style='background-color: #fcb974; color: white; padding: 10px; border-radius: 5px; display: inline-block;'>"
                    + code + "</h3>"
                    + "<p>Si ce n'était pas vous, veuillez sécuriser votre compte immédiatement.</p>"
                    + "<p>Cordialement,<br>Votre équipe de sécurité</p>"
                    + "</body></html>";

            helper.setFrom("portfolio-api@outlook.com");
            helper.setTo(customer.getEmail());
            helper.setSubject("Code de validation");
            helper.setText(messageContent, true);

            // Envoyer le message
            javaMailSender.send(message);
            log.info("Code validation send to customer {}", customer.getId());

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
