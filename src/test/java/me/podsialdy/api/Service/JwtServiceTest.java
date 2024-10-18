package me.podsialdy.api.Service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.UUID;

import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Entity.Role;
import me.podsialdy.api.Entity.RoleEnum;
import me.podsialdy.api.Repository.RefreshTokenRepository;
import me.podsialdy.api.Utils.JwtConfig;

/**
 * JwtServiceTest
 */
@Slf4j
public class JwtServiceTest {

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @Mock
  private JwtConfig jwtConfig;

  @InjectMocks
  private JwtService jwtService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // Configurer les valeurs renvoyées par les méthodes du mock jwtConfig
    Mockito.when(jwtConfig.getExpirationDuration()).thenReturn(3600000L); // Exemple de valeur de durée
    Mockito.when(jwtConfig.getClaimRoles()).thenReturn("roles");
    Mockito.when(jwtConfig.getClaimScope()).thenReturn("scope");
    Mockito.when(jwtConfig.getClaimSession()).thenReturn("session");
    Mockito.when(jwtConfig.getAuthScope()).thenReturn("auth");
    Mockito.when(jwtConfig.getIssuer()).thenReturn("issuer");
    Mockito.when(jwtConfig.getSecretKey()).thenReturn("secretKey");
    Mockito.when(jwtConfig.getPreAuthScope()).thenReturn("preAuthScope");

    // Créer l'instance de JwtService avec les valeurs moquées
    jwtService = new JwtService(refreshTokenRepository, jwtConfig);
  }

  @Test
  public void test_JwtService_generateToken() {

    Role role = new Role();
    role.setRole(RoleEnum.USER.name());
    Set<Role> roles = Set.of(role);
    Customer customer = Customer.builder()
        .roles(roles)
        .id(UUID.randomUUID())
        .username("Antoine")
        .password("PassRand123.")
        .email("test@gmail.com").build();

    String jwt = jwtService.generateToken(customer);
    log.info(jwt);
    assertNotNull(jwt);

  }

}
