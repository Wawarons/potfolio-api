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
public class CodeValidationTest {
    

    private Validator validator;

    @BeforeAll
    public void init_setup() {
        log.info("CodeValidationDTO tests starting...");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public void clear() {
        log.info("CodeValidationDto tests finish");
    }

    @Test
    public void test_codeValidationDto_constructor() {
        String code = "123456";
        CodeValidationDto codeValidationDto = new CodeValidationDto(code);
        assertEquals(code, codeValidationDto.getCode());
    }

    @Test
    public void test_codeValidationDto_setter() {
        String code = "123456";
        CodeValidationDto codeValidationDto = new CodeValidationDto();
        codeValidationDto.setCode(code);
        assertEquals(code, codeValidationDto.getCode());
    }

    @Test
    public void test_codeValidationDto_constraints() {
        String code = "123456";
        CodeValidationDto codeValidationDto = new CodeValidationDto(code);
        Set<ConstraintViolation<CodeValidationDto>> constraints = validator.validate(codeValidationDto);
        assertTrue(constraints.isEmpty());

        codeValidationDto.setCode("");
        constraints = validator.validate(codeValidationDto);
        assertFalse(constraints.isEmpty());

        codeValidationDto.setCode("");
        constraints = validator.validate(codeValidationDto);
        assertFalse(constraints.isEmpty());
    }



}
