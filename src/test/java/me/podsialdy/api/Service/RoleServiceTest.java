package me.podsialdy.api.Service;

import jakarta.inject.Inject;
import me.podsialdy.api.Entity.Role;
import me.podsialdy.api.Entity.RoleEnum;
import me.podsialdy.api.Repository.RoleRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_RoleService_init_role_exist() {
        // Arrange
        RoleEnum roleEnum = RoleEnum.ADMIN;
        Role existingRole = new Role();
        existingRole.setRole(roleEnum.name());

        when(roleRepository.findByRole(any())).thenReturn(Optional.of(existingRole));

        // Act
        Role result = roleService.initRole(roleEnum);

        // Assert
        assertEquals(existingRole, result);
        verify(roleRepository, never()).save(any(Role.class)); // Verify save() is not called
    }

    @Test
    public void test_RoleService_init_role_not_exist() {
        RoleEnum roleEnum = RoleEnum.ADMIN;
        Role role = new Role();
        role.setRole(roleEnum.name());
        when(roleRepository.findByRole(any())).thenReturn(Optional.empty());

        Role result = roleService.initRole(roleEnum);

        assertEquals(role, result);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

//
//    public Role initRole(RoleEnum newRole) {
//        Role role = roleRepository.findByRole(newRole.name()).orElse(null);
//        if (role == null) {
//            Role addRole = new Role();
//            addRole.setRole(newRole.name());
//            roleRepository.save(addRole);
//            role = addRole;
//        }
//
//        return role;
//
//    }

}
