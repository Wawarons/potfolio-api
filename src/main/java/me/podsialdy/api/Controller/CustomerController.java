package me.podsialdy.api.Controller;

import java.time.Instant;
import java.util.Objects;

import me.podsialdy.api.Utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.DTO.UserInfoTokenDto;
import me.podsialdy.api.Service.CookieService;
import me.podsialdy.api.Service.CustomerService;

@RestController
@RequestMapping(path = "user")
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CookieService cookieService;

    @GetMapping(path = "/info")
    public ResponseEntity<?> getCustomerInfo(HttpServletRequest request) {

        log.info("Attempt to get user info");

        String token = cookieService.getAccessToken(request);

        String userInfoToken = customerService.getCustomerInfo(token);
        if (Objects.isNull(userInfoToken)) {
            ResponseMessage responseMessage = new ResponseMessage("internal server error", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return new ResponseEntity<ResponseMessage>(responseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new UserInfoTokenDto(userInfoToken, Instant.now()), HttpStatus.OK);

    }

}
