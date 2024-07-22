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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoginDTOTest {

    private Validator validator;

    @BeforeAll
    public void init_setup() {
        log.info("LoginDTO tests starting...");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

    }

    @AfterAll
    public void clear() {
        log.info("LoginDTO tests finish");
    }

    @Test
    public void test_loginDto_constructor() {
        String username = "Username";
        String password = "RandomPass123.";
        LoginDto loginDto = new LoginDto(username, password);

        assertEquals(password, loginDto.getPassword());
        assertEquals(username, loginDto.getUsername());
    }

    @Test
    public void test_loginDto_setter() {
        String username = "Username";
        String password = "RandomPass123.";

        LoginDto loginDto = new LoginDto();

        loginDto.setPassword(password);
        loginDto.setUsername(username);

        assertEquals(password, loginDto.getPassword());
        assertEquals(username, loginDto.getUsername());
    }

    @Test
    public void test_loginDto_constraints() {
        String username = "Username";
        String password = "RandomPass123.";

        LoginDto loginDto = new LoginDto(username, password);
        Set<ConstraintViolation<LoginDto>> constraints = validator.validate(loginDto);

        assertTrue(constraints.isEmpty());

        loginDto.setPassword(null);
        constraints = validator.validate(loginDto);
        assertFalse(constraints.isEmpty());
        
    }



    
}
