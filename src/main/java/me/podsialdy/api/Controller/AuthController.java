package me.podsialdy.api.Controller;

import java.util.Optional;
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
   * @param customerRegisterDto {@link me.podsialdy.api.DTO.CustomerRegisterDto}
   *                            user's informations
   *
   */
  @PostMapping(path = "register")
  public ResponseEntity<?> register(@Valid @RequestBody CustomerRegisterDto customerRegisterDto) {

    log.info("Attempt to create an user.");

    Optional<Customer> customerEmail = customerRepository.findByEmail(customerRegisterDto.getEmail());
    Optional<Customer> customerUsername = customerRepository.findByUsername(customerRegisterDto.getUsername());

    if (customerEmail.isPresent()) {
      log.error("Cannot create user email address is already taken.");
      return new ResponseEntity<>("Email already register", HttpStatus.FORBIDDEN);
    } else if (customerUsername.isPresent()) {
      log.error("Cannot create user username is already taken.");
      return new ResponseEntity<String>("Username already taken", HttpStatus.FORBIDDEN);
    }

    Customer customer = Customer.builder()
        .email(customerRegisterDto.getEmail())
        .username(customerRegisterDto.getUsername())
        .password(passwordEncoder.encode(customerRegisterDto.getPassword()))
        .roles(Set.of(roleService.initRole(RoleEnum.USER)))
        .build();

    customerRepository.save(customer);
    log.info("New registration for customer: {}", customer.getId());

    return new ResponseEntity<String>("User register successfully", HttpStatus.CREATED);

  }

  /**
   * Controller class for handling authentication related endpoints.
   * Includes methods for user registration, user login, and user logout.
   */
  @PostMapping(path = "login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
    Customer customer = customerRepository.findByUsernameOrEmail(loginDto.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    try {
      authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
      log.info("Customer {} is authenticated", customer.getId());
      String jwt = jwtService.generateToken(customer);
      cookieService.addAccesstoken(response, jwt);
      codeService.sendCodeTo(customer);

    } catch (BadCredentialsException e) {
      return new ResponseEntity<>(new ResponseDto("Bad credentials", HttpStatus.NOT_FOUND.value()),
          HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.ok().build();
  }

  /**
   * Registers a new user with the provided information.
   * 
   * @param customerRegisterDto the DTO containing registration information
   */
  @PostMapping(path = "logout")
  public ResponseEntity<?> logout(HttpServletResponse response) {
    cookieService.removeAccessToken(response);
    log.info("User deconnected");
    return ResponseEntity.ok().build();
  }

}
