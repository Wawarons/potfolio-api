package me.podsialdy.api.Controller;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.DTO.CustomerRegisterDto;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Entity.RoleEnum;
import me.podsialdy.api.Repository.CustomerRepository;
import me.podsialdy.api.Service.RoleService;

@RestController
@RequestMapping(path = "user")
@Slf4j
public class CustomerController {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private CustomerRepository customerRepository;

  @Autowired
  private RoleService roleService;

  /**
   * <p>Endpoint for register a new user</p> 
   *
   * @param customerRegisterDto {@link me.podsialdy.api.DTO.CustomerRegisterDto} customer's informations
   *
   * */
  @PostMapping(path = "register")
  public ResponseEntity<?> register(@Valid @RequestBody CustomerRegisterDto customerRegisterDto) {

    log.info("Attempt to create an user.");

    Optional<Customer> customerEmail = customerRepository.findByEmail(customerRegisterDto.getEmail());
    Optional<Customer> customerUsername = customerRepository.findByUsername(customerRegisterDto.getUsername());

    if(customerEmail.isPresent()) {
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



}
