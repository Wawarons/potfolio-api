package me.podsialdy.api.DTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class CustomerRegisterDTOTest {

    private Validator validator;

    @BeforeAll
    public void init_setup() {
        log.info("CustomerRegisterDto tests starting...");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public void clear() {
        log.info("CustomerRegisterDto tests finish");
    }

    @Test
    public void test_customerRegisterDto_constructor() {
        String username = "username";
        String email = "username@email.com";
        String password = "RandPassword123.";
        CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto(email, username, password);

        assertEquals(password, customerRegisterDto.getPassword());
        assertEquals(username, customerRegisterDto.getUsername());
        assertEquals(email, customerRegisterDto.getEmail());
    }

    @Test
    public void test_customerRegisterDto_setter() {
        String username = "username";
        String email = "username@email.com";
        String password = "RandPassword123.";

        CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto();
        customerRegisterDto.setEmail(email);
        customerRegisterDto.setPassword(password);
        customerRegisterDto.setUsername(username);

        assertEquals(password, customerRegisterDto.getPassword());
        assertEquals(username, customerRegisterDto.getUsername());
        assertEquals(email, customerRegisterDto.getEmail());

    }

    @Test
    public void test_customerRegisterDto_constraints() {

        String username = "username";
        String email = "username@email.com";
        String password = "RandomPass123.";

        CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto(email, username, password);

        Set<ConstraintViolation<CustomerRegisterDto>> constraints = validator.validate(customerRegisterDto);

        assertTrue(constraints.isEmpty());

        customerRegisterDto.setEmail("wrong format email");
        constraints = validator.validate(customerRegisterDto);
        assertFalse(constraints.isEmpty());
    }
}
