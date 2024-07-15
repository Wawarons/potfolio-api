package me.podsialdy.api.Entity;

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
    public void test_role_builder() {
        long id = 12;
        Instant now = Instant.now();
        String roleName = RoleEnum.ADMIN.name();

        Role role = Role.builder().id(id).role(roleName).createdAt(now).build();
    
        assertEquals(id, role.getId());
        assertEquals(now, role.getCreatedAt());
        assertEquals(roleName, role.getRole());
    }

    @Test
    public void test_role_toString() {
        Role role = new Role();
        String expected = "Role: " + role.getRole();
        assertEquals(expected, role.toString());
    }

    @Test
    public void test_role_setter() {
        Instant now = Instant.now();
        long id = 1;
        
        Role role = new Role();
        role.setId(id);
        role.setCreatedAt(now);
        
        assertEquals(now, role.getCreatedAt());
        assertEquals(id, role.getId());
    }

    @Test
    public void test_role_equals() {
        Role role1 = new Role(1, RoleEnum.ADMIN.name(), Instant.now());
        Role role2 = new Role(2, RoleEnum.USER.name(), Instant.now());
        Role role3 = new Role(2, RoleEnum.ADMIN.name(), Instant.now());
        Role role4 = new Role(2, RoleEnum.USER.name(), Instant.now());

        role1.setRole(RoleEnum.ADMIN.name());
        role2.setRole(RoleEnum.USER.name());

        String wrongInstance = "blabla";

        assertFalse(role1.equals(wrongInstance));
        assertFalse(role1.equals(role2)); // Not the same Id
        assertFalse(role2.equals(role3)); // Not the same Role
        assertFalse(role1.equals(role3)); // Not the same Id and not the same role
        assertTrue(role1.equals(role1)); // Same object
        assertTrue(role2.equals(role4)); // Same id and same role
    }
}
