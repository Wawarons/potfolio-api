package me.podsialdy.api.Service;

import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {
    @Mock
    private JwtService jwtService;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_CustomerService_get_info() {

        String token = "fake-token";
        String userInfo = "user-info";
        when(jwtService.getUserToken(token)).thenReturn(userInfo);

        assertEquals(customerService.getCustomerInfo(token), userInfo);

    }

    @Test
    public void test_CustomerService_set_customer_verification() {
        Customer customer = Customer.builder().username("Antoine").email("email@test.com").password("Password123.").isVerified(false).build();
        customerService.setCustomerVerification(customer, true);
        verify(customerRepository, times(1)).save(customer);
        assertTrue(customer.isVerified());
    }
}
