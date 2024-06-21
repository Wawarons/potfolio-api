package me.podsialdy.api.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.DTO.CodeValidationDto;
import me.podsialdy.api.DTO.ResponseDto;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Repository.CustomerRepository;
import me.podsialdy.api.Service.CodeService;
import me.podsialdy.api.Service.CookieService;
import me.podsialdy.api.Service.JwtService;

/**
 * CodeController it's a {@link RestController} that handles endpoints related
 * to the validation codes
 * 
 */
@RestController
@Slf4j
public class CodeController {

    private JwtService jwtService;
    private CookieService cookieService;
    private CustomerRepository customerRepository;
    private CodeService codeService;

    @Autowired
    public CodeController(JwtService jwtService, CookieService cookieService, CustomerRepository customerRepository,
            CodeService codeService) {
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.customerRepository = customerRepository;
        this.codeService = codeService;
    }

    /**
     *
     * Handle the user's code validation requests
     *
     * 
     * @param codeValidationDto {@link CodeValidationDto} Contain the validation
     *                          code send by the user
     * @param request           {@link HttpServletRequest} Contain informations
     *                          about user's request
     * @param response          {@link HttpServletResponse} Response sent to the
     *                          user with the updated cookie
     * @return {@link ResponseEntity}
     */
    @PostMapping(path = "auth/code/validation")
    public ResponseEntity<ResponseDto> authValidation(@Valid @RequestBody CodeValidationDto codeValidationDto,
            HttpServletRequest request, HttpServletResponse response) {

        log.info("Code validation request");
        String token = cookieService.getAccessToken(request);
        if (token == null && !jwtService.verifyToken(token)) {
            log.error("Token invalid");
            return new ResponseEntity<>(new ResponseDto("Access denied", HttpStatus.FORBIDDEN.value()),
                    HttpStatus.FORBIDDEN);
        }

        String username = jwtService.getSubject(token);
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (codeService.validateCode(customer, codeValidationDto.getCode())) {
            if (!customer.isVerified()) {
                customer.setVerified(true);
                customerRepository.save(customer);
            }
            String jwt = jwtService.grantAccessToken(token);
            cookieService.addAccesstoken(response, jwt);

            log.info("Customer {} code validate", customer.getId());
            return ResponseEntity.ok().build();
        }
        log.error("Code invalid");
        return new ResponseEntity<>(new ResponseDto("Code invalid", HttpStatus.BAD_REQUEST.value()),
                HttpStatus.BAD_REQUEST);

    }

    /**
     *
     * Handle the user's new code request
     *
     * 
     * @param request {@link HttpServletRequest} Contain informations about user's
     *                request
     * @return {@link ResponseEntity}
     */
    @GetMapping(path = "auth/code/claim")
    public ResponseEntity<ResponseDto> claimCode(HttpServletRequest request) {
        String token = cookieService.getAccessToken(request);
        if (token == null && !jwtService.verifyToken(token)) {
            log.error("Token invalid");
            return new ResponseEntity<>(new ResponseDto("Access denied", HttpStatus.FORBIDDEN.value()),
                    HttpStatus.FORBIDDEN);
        }

        String username = jwtService.getSubject(token);
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        codeService.sendCodeTo(customer);
        return ResponseEntity.ok().build();
    }

}
