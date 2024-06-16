package me.podsialdy.api.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private JwtCodeFilter jwtCodeFilter;


    @Bean
    public FilterRegistrationBean<JwtCodeFilter> loggingFilter(){
        FilterRegistrationBean<JwtCodeFilter> registrationBean 
          = new FilterRegistrationBean<>();

        registrationBean.setFilter(jwtCodeFilter);
        registrationBean.addUrlPatterns("/code/auth/validation/*");

        return registrationBean;    
    }


}
