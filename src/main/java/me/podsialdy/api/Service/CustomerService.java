package me.podsialdy.api.Service;

import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerRepository customerRepository;

    public String getCustomerInfo(String token) {
        return jwtService.getUserToken(token);
     }

     public void setCustomerVerification(Customer customer, boolean isVerified){
        customer.setVerified(isVerified);
        customerRepository.save(customer);
     }
    
}
