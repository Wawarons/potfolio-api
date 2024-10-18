package me.podsialdy.api.Service;

import me.podsialdy.api.Entity.Code;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Repository.CodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CodeServiceTest {

  @Mock
  private CodeRepository codeRepository;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private CodeService codeService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void test_CodeService_sendCodeTo() {
    Customer customer = Customer.builder().username("Antoine").email("test@gmail.com").password("PassRand123.").build();

    codeService.sendCodeTo(customer);
    verify(codeRepository, times(1))
        .save(argThat(code -> code.getCustomer().equals(customer) && !code.getCode().isEmpty()));
    verify(emailService, times(1)).sendValidationCode(eq(customer), anyString());
  }

  @Test
  public void test_CodeService_generate_code() {
    assertEquals(codeService.generateCode().length(), 6);
  }

  @Test
  public void test_CodeService_validateCode_valid() {
    Customer customer = Customer.builder().id(UUID.randomUUID()).username("Antoine").email("test@gmail.com")
        .password("PassRand123.").build();
    Instant expiration = Instant.now().plus(5L, ChronoUnit.MINUTES);
    Code code = Code.builder().customer(customer).code("123456").isUsed(false).expiration(expiration).build();
    when(codeRepository.findFirstByCustomerIdOrderByCreatedAtDesc(customer.getId())).thenReturn(List.of(code));

    assertFalse(code.isUsed());

    boolean result = codeService.validateCode(customer, code.getCode());

    verify(codeRepository, times(1)).save(argThat(codeArg -> codeArg.equals(code)));
    assertTrue(code.isUsed());
    assertTrue(result);

  }

  @Test
  public void test_CodeService_validateCode_invalid() {
    Customer customer = Customer.builder().id(UUID.randomUUID()).username("Antoine").email("test@gmail.com")
        .password("PassRand123.").build();
    Instant expiration = Instant.now().plus(5L, ChronoUnit.MINUTES);
    Code code = Code.builder().customer(customer).code("123456").isUsed(false).expiration(expiration).build();
    when(codeRepository.findFirstByCustomerIdOrderByCreatedAtDesc(customer.getId())).thenReturn(List.of(code));

    assertFalse(code.isUsed());

    boolean result = codeService.validateCode(customer, "000000");

    verify(codeRepository, never()).save(any());
    assertFalse(result);
  }

  @Test
  public void test_CodeService_validateCode_expiration() {
    Customer customer = Customer.builder().id(UUID.randomUUID()).username("Antoine").email("test@gmail.com")
        .password("PassRand123.").build();
    Instant expiration = Instant.now().minus(5L, ChronoUnit.MINUTES);
    Code code = Code.builder().customer(customer).code("123456").isUsed(false).expiration(expiration).build();
    when(codeRepository.findFirstByCustomerIdOrderByCreatedAtDesc(customer.getId())).thenReturn(List.of(code));

    assertFalse(code.isUsed());

    boolean result = codeService.validateCode(customer, code.getCode());

    verify(codeRepository, never()).save(any());
    assertFalse(result);
  }

  @Test
  public void test_CodeService_validateCode_is_used() {
    Customer customer = Customer.builder().id(UUID.randomUUID()).username("Antoine").email("test@gmail.com")
        .password("PassRand123.").build();
    Instant expiration = Instant.now().plus(5L, ChronoUnit.MINUTES);
    Code code = Code.builder().customer(customer).code("123456").isUsed(true).expiration(expiration).build();
    when(codeRepository.findFirstByCustomerIdOrderByCreatedAtDesc(customer.getId())).thenReturn(List.of(code));

    assertTrue(code.isUsed());

    boolean result = codeService.validateCode(customer, code.getCode());

    verify(codeRepository, never()).save(any());
    assertFalse(result);
  }

}
