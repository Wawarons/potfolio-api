package me.podsialdy.api.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Repository.CustomerRepository;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        Customer customer = customerRepository.findByUsernameOrEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserDetails user = User.builder()
        .username(customer.getUsername())
        .password(customer.getPassword())
        .authorities(customer.getAuthorities())
        .accountLocked(customer.isBlock())
        .build();

        return user;
    }
    

    
}
