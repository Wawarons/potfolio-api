package me.podsialdy.api.DTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
public class ResponseDTOTest {

    // Test
    // Code -> regex -> notnull
    // message -> size -> not null
    // constucteur

    private Validator validator;

    @BeforeAll
    public void init_setup() {
        log.info("Tests ResponseDTO starting...");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public void clear() {
        log.info("Response tests finish");
    }

    @Test
    public void test_ResponseDTO_constructor() {
        String messageExpected = "Invalid code";
        int codeExpected = 403;
        ResponseDto responseDto = new ResponseDto(messageExpected, codeExpected);
        assertEquals(messageExpected, responseDto.getMessage());
        assertEquals(codeExpected, responseDto.getCode());
    }


    @Test
    public void test_ResponseDto_setter() {
        String message = "Invalid code";
        int code = 403;
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage(message);
        responseDto.setCode(code);

        assertEquals(message, responseDto.getMessage());
        assertEquals(code, responseDto.getCode());
    }

    @Test
    public void test_ResponseDto_code() {
        String message = "Invalid code";
        int code = 403;
        ResponseDto responseDto = new ResponseDto(message, code);
        Set<ConstraintViolation<ResponseDto>> constraints = validator.validate(responseDto);
        assertTrue(constraints.isEmpty());

        responseDto.setCode(0);
        constraints = validator.validate(responseDto);
        assertFalse(constraints.isEmpty());
    }

    @Test
    public void test_ResponseDto_message() {
        String message = "Invalid code";
        int code = 403;
        ResponseDto responseDto = new ResponseDto(message, code);
        Set<ConstraintViolation<ResponseDto>> constraints = validator.validate(responseDto);
        assertTrue(constraints.isEmpty());

        responseDto.setMessage("not");
        constraints = validator.validate(responseDto);
        assertFalse(constraints.isEmpty());
    }

    @Test
    public void test_ResponseDto_builder() {
        int code = HttpStatus.FORBIDDEN.value();
        String message = "message";
        Instant instant = Instant.now().plus(5L, ChronoUnit.SECONDS);
        ResponseDto responseDto = ResponseDto.builder().code(code).message(message).timestamp(instant).build();

        assertEquals(message, responseDto.getMessage());
        assertEquals(code, responseDto.getCode());
        assertEquals(instant, responseDto.getTimestamp());

        code = HttpStatus.OK.value();
        message = "new message";
        instant = Instant.now();

        responseDto.setCode(code);
        responseDto.setMessage(message);
        responseDto.setTimestamp(instant);

        assertEquals(message, responseDto.getMessage());
        assertEquals(code, responseDto.getCode());
        assertEquals(instant, responseDto.getTimestamp());
    }

}
