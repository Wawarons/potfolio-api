package me.podsialdy.api.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private JwtService jwtService;

    public String getCustomerInfo(String token) {
        String userInfoToken = jwtService.getUserToken(token);
         return userInfoToken;
     }
    
}
