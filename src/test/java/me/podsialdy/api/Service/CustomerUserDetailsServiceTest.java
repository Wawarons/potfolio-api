package me.podsialdy.api.Service;

import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CustomerUserDetailsServiceTest {


    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerUserDetailsService customerUserDetailsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

   @Test
   public void test_CustomerUserDetailsService_load_user() {
      Customer customer = Customer.builder().username("Antoine").email("email@test.com").password("PassRand123.").build();

      when(customerRepository.findByUsernameOrEmail(customer.getUsername())).thenReturn(Optional.of(customer));

      UserDetails user = customerUserDetailsService.loadUserByUsername(customer.getUsername());

      assertNotNull(user);
      assertEquals(user.getUsername(), customer.getUsername());
      assertEquals(user.getPassword(), customer.getPassword());
      assertEquals(user.isAccountNonLocked(), !customer.isBlock());


   }

   @Test
    public void test_CustomerUserDetailsService_load_user_not_found() {
        when(customerRepository.findByUsernameOrEmail("something")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
           customerUserDetailsService.loadUserByUsername("something");
        });
   }
}
