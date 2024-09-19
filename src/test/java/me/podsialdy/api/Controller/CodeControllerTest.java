package me.podsialdy.api.Controller;

import com.google.gson.Gson;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import me.podsialdy.api.DTO.CodeValidationDto;
import me.podsialdy.api.DTO.ResponseDto;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Error.GlobalExceptionHandle;
import me.podsialdy.api.Repository.CustomerRepository;
import me.podsialdy.api.Security.JwtCodeFilter;
import me.podsialdy.api.Service.CodeService;
import me.podsialdy.api.Service.CookieService;
import me.podsialdy.api.Service.JwtService;
import me.podsialdy.api.Service.RefreshTokenService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GlobalExceptionHandle globalExceptionHandle;

    @Autowired
    private CodeController codeController;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CookieService cookieService;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private CodeService codeService;

    @MockBean
    private RefreshTokenService refreshTokenService;

    @MockBean
    private JwtCodeFilter jwtCodeFilter;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(codeController)
                .setControllerAdvice(globalExceptionHandle)
                .build();
    }


    @Test
    public void test_codeController_token_null() throws Exception {
        when(cookieService.getAccessToken(any())).thenReturn(null);
        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());


        MvcResult result = mockMvc.perform(get("/auth/code/claim"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied")).andReturn();
        ;
    }

    @Test
    public void test_codeController_token_invalid() throws Exception {

        String token = "fake_token";

        when(cookieService.getAccessToken(any())).thenReturn(token);
        when(jwtService.verifyToken(token)).thenReturn(false);
        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());

        MvcResult result = mockMvc.perform(get("/auth/code/claim"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied")).andReturn();
        ;
    }

    @Test
    public void test_codeController_token_valid() throws Exception {

        Customer customer = Customer.builder().id(UUID.randomUUID()).username("John").email("email@test.com").password("Password123.").build();

        String token = "fake_token";

        when(cookieService.getAccessToken(any())).thenReturn(token);
        when(jwtService.verifyToken(token)).thenReturn(true);
        when(customerRepository.findByUsername(any())).thenReturn(Optional.of(customer));
        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());

        mockMvc.perform(get("/auth/code/claim"))
                .andExpect(status().isOk());
        ;

        verify(codeService, times(1)).sendCodeTo(any());
    }


    @Test
    public void test_codeController_code_invalid_token() throws Exception {

        String token = "fake-token";

        when(cookieService.getAccessToken(any())).thenReturn(token);
        when(jwtService.verifyToken(token)).thenReturn(false);
        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());

        CodeValidationDto codeValidationDto = new CodeValidationDto("123456");

        mockMvc.perform(post("/auth/code/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(codeValidationDto))
        ).andExpect(status().isForbidden()).andExpect(jsonPath("$.message").value("Access denied"));

    }

    @Test
    public void test_codeController_code_token_null() throws Exception {

        when(cookieService.getAccessToken(any())).thenReturn(null);
        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());


        CodeValidationDto codeValidationDto = new CodeValidationDto("123456");

        mockMvc.perform(post("/auth/code/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(codeValidationDto))
        ).andExpect(status().isForbidden()).andExpect(jsonPath("$.message").value("Access denied"));

    }

    @Test
    public void test_codeController_code_user_not_found() throws Exception {

        String token = "fake-token";

        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());

        when(cookieService.getAccessToken(any())).thenReturn(token);
        when(jwtService.verifyToken(token)).thenReturn(true);
        when(customerRepository.findByUsername(any())).thenReturn(Optional.empty());


        CodeValidationDto codeValidationDto = new CodeValidationDto("123456");

        MvcResult result = mockMvc.perform(post("/auth/code/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(codeValidationDto))
        ).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("User not found")).andReturn();
    }

    @Test
    public void test_codeController_code_invalid() throws Exception {

        String token = "fake-token";
        Customer customer = Customer.builder().id(UUID.randomUUID()).username("John").email("email@test.com").password("Password123.").build();
        CodeValidationDto codeValidationDto = new CodeValidationDto("123456");

        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());

        when(cookieService.getAccessToken(any())).thenReturn(token);
        when(jwtService.verifyToken(token)).thenReturn(true);
        when(customerRepository.findByUsername(any())).thenReturn(Optional.of(customer));
        when(codeService.validateCode(customer, codeValidationDto.getCode())).thenReturn(false);


        mockMvc.perform(post("/auth/code/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(codeValidationDto))
        ).andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Code invalid")).andReturn();
    }

    @Test
    public void test_codeController_code_valid_user_not_verified() throws Exception {

        String token = "fake-token";
        Customer customer = Customer.builder().id(UUID.randomUUID()).username("John").email("email@test.com").password("Password123.").build();
        CodeValidationDto codeValidationDto = new CodeValidationDto("123456");

        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());

        when(cookieService.getAccessToken(any())).thenReturn(token);
        when(jwtService.verifyToken(token)).thenReturn(true);
        when(customerRepository.findByUsername(any())).thenReturn(Optional.of(customer));
        when(codeService.validateCode(customer, codeValidationDto.getCode())).thenReturn(true);


        mockMvc.perform(post("/auth/code/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(codeValidationDto))
        ).andExpect(status().isOk());

        assertTrue(customer.isVerified());
    }

    @Test
    public void test_codeController_code_valid_user_verified() throws Exception {

        String token = "fake-token";
        Customer customer = Customer.builder().id(UUID.randomUUID()).isVerified(true).username("John").email("email@test.com").password("Password123.").build();
        CodeValidationDto codeValidationDto = new CodeValidationDto("123456");

        doNothing().when(jwtCodeFilter).doFilter(any(), any(), any());

        when(cookieService.getAccessToken(any())).thenReturn(token);
        when(jwtService.verifyToken(token)).thenReturn(true);
        when(customerRepository.findByUsername(any())).thenReturn(Optional.of(customer));
        when(codeService.validateCode(customer, codeValidationDto.getCode())).thenReturn(true);


        mockMvc.perform(post("/auth/code/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(codeValidationDto))
        ).andExpect(status().isOk());
    }


//    auth/code/validation
//    public ResponseEntity<ResponseDto> authValidation(@Valid @RequestBody CodeValidationDto codeValidationDto,
//                                                      HttpServletRequest request, HttpServletResponse response) {
//
//        log.info("Code validation request");
//        String token = cookieService.getAccessToken(request);
//        if (token == null || !jwtService.verifyToken(token)) {
//            log.error("Token invalid");
//            return new ResponseEntity<>(new ResponseDto("Access denied", HttpStatus.FORBIDDEN.value()),
//                    HttpStatus.FORBIDDEN);
//        }
//
//        String username = jwtService.getSubject(token);
//        Customer customer = customerRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//        if (codeService.validateCode(customer, codeValidationDto.getCode())) {
//            if (!customer.isVerified()) {
//                customer.setVerified(true);
//                customerRepository.save(customer);
//            }
//            String jwt = jwtService.grantAccessToken(token);
//            cookieService.addAccessToken(response, jwt);
//            log.info("The jwt value is: {}", jwt);
//            refreshTokenService.initRefreshToken(customer, jwtService.getSession(jwt));
//            log.info("Customer {} code validate", customer.getId());
//            return ResponseEntity.ok().build();
//        }
//        log.error("Code invalid");
//        return new ResponseEntity<>(new ResponseDto("Code invalid", HttpStatus.BAD_REQUEST.value()),
//                HttpStatus.BAD_REQUEST);
//
//    }

}
