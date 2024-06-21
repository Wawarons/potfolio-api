package me.podsialdy.api.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up a filter to intercept requests for
 * validating and claiming authentication codes.
 */
@Configuration
public class FilterConfig {

  @Autowired
  private JwtCodeFilter jwtCodeFilter;

  @Bean
  public FilterRegistrationBean<JwtCodeFilter> loggingFilter() {
    FilterRegistrationBean<JwtCodeFilter> registrationBean = new FilterRegistrationBean<>();

    registrationBean.setFilter(jwtCodeFilter);
    registrationBean.addUrlPatterns("/auth/code/validation", "/auth/code/claim");

    return registrationBean;
  }

}
