package me.podsialdy.api.Security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.podsialdy.api.Service.CustomerUserDetailsService;
import me.podsialdy.api.Service.JwtService;

@Configuration
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    private final String[] pathsToFilter = { "/code/auth" };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getAccessToken(request);

        if (token == null || !jwtService.verifyToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            DecodedJWT jwt = JWT.decode(token);

            if (!jwt.getClaim("scope").asString().equals("auth")) {
                filterChain.doFilter(request, response);
                return;
            }

            Authentication authUser = authenticateUSer(jwt.getSubject());
            SecurityContextHolder.getContext().setAuthentication(authUser);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }
    }

    private String getAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private Authentication authenticateUSer(String username) {
        UserDetails user = customerUserDetailsService.loadUserByUsername(username);
        System.out.println(user.getAuthorities());
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        for (String pathToNotFilter : pathsToFilter) {
            if (path.contains(pathToNotFilter)) {
                System.out.println("Not filter");
                return true;
            }
        }
        return false;
    }
}
