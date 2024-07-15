package me.podsialdy.api.Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CodeTest {

    private Validator validator;

    @BeforeAll
    public void setUp() {
        log.info("Code tests starting...");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public void clear() {
        log.info("Code tests finish");
        validator = null;
    }

    @Test
    public void test_code_constructor() {
        Customer customer = mock(Customer.class);
        Code code = new Code(UUID.randomUUID(), "123456", customer, false, Instant.now(), Instant.now());
        Set<ConstraintViolation<Code>> constraints = validator.validate(code);
        assertTrue(constraints.isEmpty());
    }

    @Test
    public void test_code_constructor_invalid_args() {
        Customer customer = mock(Customer.class);
        Code code = new Code(UUID.randomUUID(), null, customer, false, null, Instant.now());
        Set<ConstraintViolation<Code>> constraints = validator.validate(code);
        assertFalse(constraints.isEmpty());
    }

    @Test
    public void test_code_setter() {

        UUID id = UUID.randomUUID();
        String codeValue = "123456";
        Customer customer = mock(Customer.class);
        Instant now = Instant.now();
        Instant expiration = now.plus(5L, ChronoUnit.MINUTES);

        
        Code code = new Code();
        
        code.setId(id);
        code.setCode(codeValue);
        code.setCustomer(customer);
        code.setExpiration(expiration);
        code.setUsed(false);
        code.setCreatedAt(now);

        assertEquals(id, code.getId());
        assertEquals(codeValue, code.getCode());
        assertEquals(customer, code.getCustomer());
        assertEquals(expiration, code.getExpiration());
        assertEquals(false, code.isUsed());
        assertEquals(now, code.getCreatedAt());

    }

    @Test
    public void test_code_builder() {
        UUID idValue = UUID.randomUUID();
        String codeValue = "123456";
        Customer customer = mock(Customer.class);
        Instant nowTime = Instant.now();
        Instant expirationTime = nowTime.plus(5L, ChronoUnit.MINUTES);

        Code code = Code.builder().id(idValue).code(codeValue).customer(customer).isUsed(false).expiration(expirationTime).createdAt(nowTime).build();

        assertEquals(idValue, code.getId());
        assertEquals(codeValue, code.getCode());
        assertEquals(false, code.isUsed());
        assertEquals(customer, code.getCustomer());
        assertEquals(nowTime, code.getCreatedAt());
        assertEquals(expirationTime, code.getExpiration());

    }

    @Test
    public void test_code_string() {
        Customer customer = mock(Customer.class);
        Code code = new Code();
        code.setCode("123456");
        code.setCustomer(customer);

        when(customer.getUsername()).thenReturn("username");

        String expected = "Code{" +
                "id=" + code.getId() +
                ", code='" + code.getCode() + '\'' +
                ", customer=" + code.getCustomer().getUsername() +
                ", isUsed=" + code.isUsed() +
                '}';

        assertEquals(expected, code.toString());
    }

    @Test
    public void test_code_equals() {
        Code code1 = new Code();
        Code code2 = new Code();
  
        code1.setId(UUID.randomUUID());
        code2.setId(UUID.randomUUID());
  
        String wrongInstance = "blabla";
  
        assertFalse(code1.equals(wrongInstance));
        assertFalse(code1.equals(code2));
        assertTrue(code1.equals(code1));
    }

}
