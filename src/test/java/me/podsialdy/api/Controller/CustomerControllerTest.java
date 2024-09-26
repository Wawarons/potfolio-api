package me.podsialdy.api.Controller;

import jakarta.servlet.http.HttpServletRequest;
import me.podsialdy.api.DTO.UserInfoTokenDto;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Security.JwtCodeFilter;
import me.podsialdy.api.Service.CookieService;
import me.podsialdy.api.Service.CustomerService;
import me.podsialdy.api.Service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.web.bind.annotation.GetMapping;

import java.time.Instant;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerController customerController;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JwtCodeFilter jwtCodeFilter;

    @MockBean
    private CookieService cookieService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    public void test_CustomerController_get_info_success() throws Exception {
        String token = "fake_token";
        String userInfoToken = "user-info-token";
        Customer customer = Customer.builder().email("test@gmail.com").password("PassRand123.").username("Antoine").build();
        when(cookieService.getAccessToken(any(HttpServletRequest.class))).thenReturn(token);
        when(customerService.getCustomerInfo(token)).thenReturn(userInfoToken);
        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());
        mockMvc.perform(get("/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").value(userInfoToken))
        ;
    }

    @Test
    public void test_CustomerController_get_info_failed() throws  Exception {
        when(cookieService.getAccessToken(any(HttpServletRequest.class))).thenReturn(null);
        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());

        mockMvc.perform(get("/user/info"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("internal server error"));
    }
}
