package me.podsialdy.api.Service;

import java.time.Instant;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Entity.Code;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Repository.CodeRepository;

@Service
@Slf4j
public class CodeService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private CodeRepository codeRepository;

    public void sendCodeTo(Customer customer) {
        log.info("Generate code for customer {}", customer.getId());
        Code code = Code.builder().code(generateCode()).customer(customer).build();
        codeRepository.save(code);
        emailService.sendValidationCode(customer, code.getCode());
        log.info("New code sent to customer {}", customer.getId());
    }

    public String generateCode() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000); 
        String formattedCode = String.format("%06d", randomNumber);
        return formattedCode;
    }

    public boolean validateCode(Customer customer, String userCode) {
        Code code = codeRepository.findFirstByCustomerIdOrderByCreatedAtDesc(customer.getId()).getFirst();
        log.info("Customer {} attempt to validate a code", customer.getId());
        if(code.getCode().equals(userCode) && !code.isUsed() && code.getExpiration().isAfter(Instant.now())) {
            code.setUsed(true);
            codeRepository.save(code);
            log.info("Customer {} code validate", customer.getId());
            return true;
        }
        log.warn("Customer {} code invalid", customer.getId());
        return false;

    } 
    
}
