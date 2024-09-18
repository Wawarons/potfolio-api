package me.podsialdy.api.Entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
public class RoleEnumTest {


    @Test
    public void test_roleEnum_values() {
        assertNotNull(RoleEnum.ADMIN);
        assertNotNull(RoleEnum.USER);
    }


}
