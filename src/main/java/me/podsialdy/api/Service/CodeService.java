package me.podsialdy.api.Service;

import java.time.Instant;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Entity.Code;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Repository.CodeRepository;

/**
 * Service class for generating and validating codes for customers.
 * Utilizes EmailService for sending validation codes via email and
 * CodeRepository for storing and retrieving codes.
 * Provides methods for generating a random code, sending the code to a
 * customer, and validating a code entered by a customer.
 */
@Service
@Slf4j
public class CodeService {

  private EmailService emailService;
  private CodeRepository codeRepository;

  @Autowired
  public CodeService(EmailService emailService, CodeRepository codeRepository) {
    this.emailService = emailService;
    this.codeRepository = codeRepository;
  }

  /**
   * Generates a random code for the provided customer, saves the code in the
   * database,
   * and sends it to the customer via email for validation.
   * 
   * @param customer The customer to whom the code will be sent
   */
  public void sendCodeTo(Customer customer) {
    log.info("Generate code for customer {}", customer.getId());
    Code code = Code.builder().code(generateCode()).customer(customer).build();
    codeRepository.save(code);
    emailService.sendValidationCode(customer, code.getCode());
    log.info("New code sent to customer {}", customer.getId());
  }

  /**
   * Generates a random 6-digit validation code for a customer.
   * 
   * @return The randomly generated 6-digit validation code
   */
  public String generateCode() {
    Random random = new Random();
    int randomNumber = random.nextInt(1000000);
    String formattedCode = String.format("%06d", randomNumber);
    return formattedCode;
  }

  /**
   * Validates the code entered by a customer.
   * 
   * @param customer The customer who is attempting to validate the code
   * @param userCode The code entered by the customer for validation
   * @return true if the code is valid and has not expired, false otherwise
   */
  public boolean validateCode(Customer customer, String userCode) {
    Code code = codeRepository.findFirstByCustomerIdOrderByCreatedAtDesc(customer.getId()).get(0);
    log.info("Customer {} attempt to validate a code", customer.getId());
    if (code.getCode().equals(userCode) && !code.isUsed() && code.getExpiration().isAfter(Instant.now())) {
      code.setUsed(true);
      codeRepository.save(code);
      log.info("Customer {} code validate", customer.getId());
      return true;
    }
    log.warn("Customer {} code invalid", customer.getId());
    return false;

  }

}
