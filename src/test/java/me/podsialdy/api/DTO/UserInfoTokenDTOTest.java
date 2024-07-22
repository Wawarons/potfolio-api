package me.podsialdy.api.DTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
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

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserInfoTokenDTOTest {

    private Validator validator;
    @BeforeAll
    public void init_setup() {
        log.info("UserInfoTokenDto tests starting...");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public void clear() {
        log.info("USerInfoToken tests finish");
    }

    @Test
    public void test_userInfoTokenDto_constructor() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflK";
        UserInfoTokenDto userInfoTokenDto = new UserInfoTokenDto(token, Instant.now());

        assertEquals(token, userInfoTokenDto.getToken());
    }

    @Test
    public void test_userInfoTokenDto_setter() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflK";
        Instant now = Instant.now();
        UserInfoTokenDto userInfoTokenDto = new UserInfoTokenDto();
        userInfoTokenDto.setToken(token);
        userInfoTokenDto.setTimestamp(now);
        assertEquals(token, userInfoTokenDto.getToken());
        assertEquals(now, userInfoTokenDto.getTimestamp());

    }

    @Test
    public void test_userInfoTokenDto_constraints() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflK";
        Instant now = Instant.now();
        UserInfoTokenDto userInfoTokenDto = new UserInfoTokenDto(token, now);
        Set<ConstraintViolation<UserInfoTokenDto>> constraints = validator.validate(userInfoTokenDto);
        assertTrue(constraints.isEmpty());

        userInfoTokenDto.setToken(null);
        constraints = validator.validate(userInfoTokenDto);
        assertFalse(constraints.isEmpty());
    }
    
}
