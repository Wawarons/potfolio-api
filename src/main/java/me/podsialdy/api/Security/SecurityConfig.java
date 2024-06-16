package me.podsialdy.api.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import me.podsialdy.api.Service.CustomerUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorize -> 
            authorize.requestMatchers("/**").permitAll()
        )
        .httpBasic(basic -> basic.disable())
        ;

        return http.build();
    } 
    

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
        CustomerUserDetailsService customUserDetailsService,
        PasswordEncoder passwordEncoder) {
      DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
      authenticationProvider.setUserDetailsService(customUserDetailsService);
      authenticationProvider.setPasswordEncoder(passwordEncoder);
  
      return new ProviderManager(authenticationProvider);
    }
  

}
