package me.podsialdy.api.Controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.DTO.CustomerRegisterDto;
import me.podsialdy.api.DTO.LoginDto;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Repository.CustomerRepository;
import me.podsialdy.api.Security.JwtCodeFilter;
import me.podsialdy.api.Service.CodeService;
import me.podsialdy.api.Service.CookieService;
import me.podsialdy.api.Service.JwtService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CookieService cookieService;

    @MockBean
    private CodeService codeService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtCodeFilter jwtCodeFilter;


    @Test
    public void test_authController_register_email_alreadyuse() throws Exception {

        CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto("email@test.com", "username", "Password123.");

        Customer customer = Customer.builder().id(UUID.randomUUID()).email(customerRegisterDto.getEmail()).password(customerRegisterDto.getPassword()).build();


        when(customerRepository.findByEmail(any())).thenReturn(Optional.of(customer));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(customerRegisterDto))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Email already register"));


    }

    @Test
    public void test_authController_register_username_alreadyuse() throws Exception {

        CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto("email@test.com", "username", "Password123.");

        Customer customer = Customer.builder().id(UUID.randomUUID()).email(customerRegisterDto.getEmail()).password(customerRegisterDto.getPassword()).build();


        when(customerRepository.findByUsername(any())).thenReturn(Optional.of(customer));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(customerRegisterDto))
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Username already taken"));


    }

    @Test
    public void test_autrhController_register_passwordformat() throws Exception{
        CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto("email@test.com", "username", "Password");

        when(customerRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(customerRegisterDto))
        ).andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void test_authController_register_success() throws Exception {

        CustomerRegisterDto customerRegisterDto = new CustomerRegisterDto("email@test.com", "username", "Password123.");

        Customer customer = Customer.builder().id(UUID.randomUUID()).email(customerRegisterDto.getEmail()).password(customerRegisterDto.getPassword()).build();


        when(customerRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(any())).thenReturn(Optional.empty());


        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(customerRegisterDto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User register successfully"));


    }


    @Test
    public void test_authController_login_success() throws Exception {

        LoginDto loginDto = new LoginDto("email@test.com", "Password123.");

        Customer customer = Customer.builder().id(UUID.randomUUID()).email(loginDto.getUsername()).password(loginDto.getPassword()).build();

        when(customerRepository.findByUsernameOrEmail(any())).thenReturn(Optional.of(customer));
        when(jwtService.generateToken(customer)).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(new Gson().toJson(loginDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void test_authController_login_failed_usernotfound() throws Exception {

        LoginDto loginDto = new LoginDto("email@test.com", "Password123.");

        // Configurer les mocks pour le cas d'échec
        when(customerRepository.findByUsernameOrEmail(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(new Gson().toJson(loginDto)))
                .andExpect(status().isNotFound());
        ;
    }

    @Test
    public void test_authController_login_failed_bad_credentials() throws Exception {

        LoginDto loginDto = new LoginDto("email@test.com", "Password123.");

        Customer customer = Customer.builder().id(UUID.randomUUID()).email(loginDto.getUsername()).password(loginDto.getPassword()).build();
        // Configurer les mocks pour le cas d'échec
        when(customerRepository.findByUsernameOrEmail(any(String.class))).thenReturn(Optional.of(customer));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(new Gson().toJson(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Bad credentials"));
        ;
    }


    @Test
    public void test_authController_logout() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())).andExpect(status().isOk());
    }

}

