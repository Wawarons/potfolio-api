package me.podsialdy.api.Entity;

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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class RoleTest {

    private Validator validator;

    @BeforeAll
    public void setUp() {
        log.info("Role tests starting...");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    public void clear() {
        log.info("Role tests finish");
        validator = null;
    }

    @Test
    public void test_role_constructor() {

        Role role = new Role(1, RoleEnum.ADMIN.name(), Instant.now());
        Set<ConstraintViolation<Role>> constraints = validator.validate(role);
        assertTrue(constraints.isEmpty());
    }

    @Test
    public void test_role_constructor_invalid_args() {

        Role role = new Role(1, null, Instant.now());
        Set<ConstraintViolation<Role>> constraints = validator.validate(role);
        assertFalse(constraints.isEmpty());
    }

    @Test
    public void test_role_toString() {
        Role role = new Role();
        String expected = "Role: " + role.getRole();
        assertEquals(role.toString(), expected);
    }

    @Test
    public void test_role_equals() {
        Role role1 = new Role(1, RoleEnum.ADMIN.name(), Instant.now());
        Role role2 = new Role(2, RoleEnum.USER.name(), Instant.now());
        Role role3 = new Role(2, RoleEnum.ADMIN.name(), Instant.now());

        role1.setRole(RoleEnum.ADMIN.name());
        role2.setRole(RoleEnum.USER.name());

        String wrongInstance = "blabla";

        assertFalse(role1.equals(wrongInstance));
        assertFalse(role1.equals(role2));
        assertFalse(role2.equals(role3));
        assertTrue(role1.equals(role1));
    }
}
