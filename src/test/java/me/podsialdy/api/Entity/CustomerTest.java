package me.podsialdy.api.Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerTest {

   private Validator validator;

   @BeforeAll
   public void setUp() {
      log.info("Customer tests starting...");
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      validator = factory.getValidator();
   }

   @AfterAll
   public void clear() {
      log.info("Customer tests finish");
      validator = null;
   }

   @Test
   public void test_set_email() {
      Customer customer = new Customer();
      customer.setEmail("email@test.com");
      assertEquals("email@test.com", customer.getEmail());
   }

   @Test
   public void test_customer_password_validation() {
      String wrongFormat = "notenoughcomplex12.";
      String goodFormat = "RandomPass123.";
      Customer customer = Customer.builder().username("username").email("test@gmail.com").build();
      
      customer.setPassword(wrongFormat);
      Set<ConstraintViolation<Customer>> constraints = validator.validate(customer);
      assertFalse(constraints.isEmpty());

      customer.setPassword(null);
      constraints = validator.validate(customer);
      assertFalse(constraints.isEmpty());
      
      customer.setPassword(goodFormat);
      constraints = validator.validate(customer);
      assertTrue(constraints.isEmpty());

   }

   @Test
   public void test_set_username() {
      Customer customer = new Customer();
      customer.setUsername("username");
      assertEquals("username", customer.getUsername());
   }

   @Test
   public void test_set_password() {
      Customer customer = new Customer();
      customer.setPassword("password");
      assertEquals("password", customer.getPassword());
   }

   @Test
   public void test_get_authorities() {
      Role roleUser = Role.builder().role(RoleEnum.USER.toString()).build();
      Role roleAdmin = Role.builder().role(RoleEnum.ADMIN.toString()).build();

      HashSet<Role> roles = new HashSet<Role>();
      roles.add(roleUser);
      roles.add(roleAdmin);

      Customer customer = new Customer();
      customer.setRoles(roles);

      customer.getAuthorities().forEach(role -> {
         assertTrue(role.getAuthority().equals("ROLE_USER") || role.getAuthority().equals("ROLE_ADMIN"));
      });

   }

   @Test
   public void test_add_role() {
      Role roleUser = Role.builder().role(RoleEnum.USER.toString()).build();
      Role roleAdmin = Role.builder().role(RoleEnum.ADMIN.toString()).build();

      HashSet<Role> roles = new HashSet<Role>();
      roles.add(roleUser);

      Customer customer = new Customer();
      customer.setRoles(roles);
      assertEquals(roles, customer.getRoles());

      customer.addRole(roleAdmin);
      assertTrue(customer.getRoles().contains(roleAdmin));

   }

   @Test
   public void test_remove_role() {
      Role roleUser = Role.builder().role(RoleEnum.USER.toString()).build();

      HashSet<Role> roles = new HashSet<Role>();
      roles.add(roleUser);

      Customer customer = new Customer();
      customer.setRoles(roles);
      assertEquals(roles, customer.getRoles());

      customer.removeRole(roleUser);
      assertFalse(customer.getRoles().contains(roleUser));
   }

   @Test
   public void test_equals() {
      Customer customer1 = new Customer();
      Customer customer2 = new Customer();

      customer1.setId(UUID.randomUUID());
      customer2.setId(UUID.randomUUID());

      String wrongInstance = "blabla";

      assertFalse(customer1.equals(wrongInstance));
      assertFalse(customer1.equals(customer2));
      assertTrue(customer1.equals(customer1));

   }

   @Test
   public void test_toString() {
      Customer customer = new Customer(UUID.randomUUID(), "test@gmail.com", "antoine", "RandomPass123.",
            new HashSet<>(), new HashSet<>(), false, false, Instant.now());
      String strCustomer = "Customer : \n\n email: " + customer.getEmail() + "\n" + "username: "
            + customer.getUsername() + "\n" + "id: " + customer.getId();
      assertEquals(customer.toString(), strCustomer);

   }

   @Test
   public void test_init_values() {
      Customer customer = new Customer();
      assertEquals(customer.getRefreshToken(), new HashSet<>());
      assertEquals(customer.isBlock(), false);
      assertEquals(customer.isVerified(), false);

   }

   // builder
   // setBlock
   // setVerified

   @Test
   public void test_set_created_at() {
      Customer customer = new Customer();
      Instant customerInstant = Instant.now();
      customer.setCreatedAt(customerInstant);
      assertEquals(customer.getCreatedAt(), customerInstant);
   }

   @Test
   public void test_set_refreshToken() {
      Customer customer = new Customer();
      assertEquals(new HashSet<>(), customer.getRefreshToken());
      Set<RefreshToken> refreshTokens = new HashSet<>();
      RefreshToken refreshToken = mock(RefreshToken.class);
      refreshTokens.add(refreshToken);
      customer.setRefreshToken(refreshTokens);
      assertEquals(refreshTokens, customer.getRefreshToken());
   }

   @Test
   public void test_set_isblock() {
      Customer customer = new Customer();
      assertFalse(customer.isBlock());
      customer.setBlock(true);
      assertTrue(customer.isBlock());
   }

   @Test
   public void test_set_isverified() {
      Customer customer = new Customer();
      assertFalse(customer.isVerified());
      customer.setVerified(true);
      assertTrue(customer.isVerified());
   }

   @Test
   public void test_builder() {
      Customer customer = Customer.builder().id(UUID.randomUUID()).roles(new HashSet<>()).refreshToken(new HashSet<>())
            .isBlock(false).isVerified(false).email("test@gmail.com").username("Antoine").password("RandomPass123.")
            .createdAt(Instant.now()).build();
      assertEquals(customer.getEmail(), "test@gmail.com");
      assertEquals(customer.getUsername(), "Antoine");
      assertEquals(customer.getPassword(), "RandomPass123.");
   }

}
