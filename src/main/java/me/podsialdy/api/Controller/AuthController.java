package me.podsialdy.api.Controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.DTO.CustomerRegisterDto;
import me.podsialdy.api.DTO.LoginDto;
import me.podsialdy.api.DTO.ResponseDto;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Entity.RoleEnum;
import me.podsialdy.api.Repository.CustomerRepository;
import me.podsialdy.api.Service.CodeService;
import me.podsialdy.api.Service.CookieService;
import me.podsialdy.api.Service.JwtService;
import me.podsialdy.api.Service.RoleService;

@RestController
@RequestMapping(path = "auth")
@Slf4j
public class AuthController {

  private PasswordEncoder passwordEncoder;
  private CustomerRepository customerRepository;
  private RoleService roleService;
  private AuthenticationManager authenticationManager;
  private JwtService jwtService;
  private CookieService cookieService;
  private CodeService codeService;

  @Autowired
  public AuthController(PasswordEncoder passwordEncoder, CustomerRepository customerRepository, RoleService roleService,
      AuthenticationManager authenticationManager, JwtService jwtService, CookieService cookieService,
      CodeService codeService) {
    this.passwordEncoder = passwordEncoder;
    this.customerRepository = customerRepository;
    this.roleService = roleService;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.cookieService = cookieService;
    this.codeService = codeService;
  }

  /**
   * Endpoint for register a new user
   *
   * @param customerRegisterDto {@link CustomerRegisterDto}
   *                            user's information
   */
  @PostMapping(path = "register")
  public ResponseEntity<?> register(@Valid @RequestBody CustomerRegisterDto customerRegisterDto) {

    log.info("Attempt to create an user.");
    ResponseDto responseDto = new ResponseDto();

    if (customerRepository.findByEmail(customerRegisterDto.getEmail()).isPresent()) {
      log.error("Cannot create user email address is already taken.");
      responseDto.setMessage("Email already register");
      responseDto.setCode(HttpStatus.FORBIDDEN.value());
      return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.FORBIDDEN);
    } else if (customerRepository.findByUsername(customerRegisterDto.getUsername()).isPresent()) {
      log.error("Cannot create user username is already taken.");
      responseDto.setMessage("Username already taken");
      responseDto.setCode(HttpStatus.FORBIDDEN.value());
      return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.FORBIDDEN);
    }

    Customer customer = Customer.builder()
        .email(customerRegisterDto.getEmail())
        .username(customerRegisterDto.getUsername())
        .password(passwordEncoder.encode(customerRegisterDto.getPassword()))
        .roles(Set.of(roleService.initRole(RoleEnum.USER)))
        .build();
    log.info("Attempt to save user");
    customerRepository.save(customer);
    log.info("New registration for customer: {}", customer.getId());
    responseDto.setMessage("User register successfully");
    responseDto.setCode(HttpStatus.CREATED.value());
    return new ResponseEntity<ResponseDto>(responseDto, HttpStatus.CREATED);

  }

  /**
   * Controller class for handling authentication related endpoints.
   * Includes methods for user registration, user login, and user logout.
   */
  @PostMapping(path = "login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {

    try {
      Customer customer = customerRepository.findByUsernameOrEmail(loginDto.getUsername())
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));
      authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
      log.info("Customer {} is authenticated", customer.getId());
      String jwt = jwtService.generateToken(customer);
      cookieService.addAccessToken(response, jwt);
      codeService.sendCodeTo(customer);

    } catch (BadCredentialsException e) {
      return new ResponseEntity<>(new ResponseDto("Bad credentials", HttpStatus.UNAUTHORIZED.value()),
          HttpStatus.UNAUTHORIZED);
    }

    return ResponseEntity.ok().build();
  }

  /**
   * Endpoint for disconnect the user.
   * 
   * @param response HttpServletResponse
   * @return ResponseEntity
   */
  @PostMapping(path = "logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
    cookieService.removeAccessToken(response);
    log.info("User deconnected");
    return ResponseEntity.ok().build();
  }

}
