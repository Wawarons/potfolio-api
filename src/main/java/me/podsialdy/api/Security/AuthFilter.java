package me.podsialdy.api.Security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Service.CustomerUserDetailsService;
import me.podsialdy.api.Service.JwtService;

/**
 * This class is responsible for filtering requests for authentication based on
 * the provided token.
 * The filter checks the validity of the token, its scope, and authenticates the
 * user accordingly.
 * It also handles the retrieval of access tokens from cookies and
 * authentication of users.
 * Additionally, it includes a method to determine whether a specific path
 * should not be filtered.
 */
@Configuration
@Slf4j
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private CustomerUserDetailsService customerUserDetailsService;

    @Value("${jwt.cookie.name}")
    private String cookieTokenName;

    @Value("${jwt.scope.auth}")
    private String authScope;

    @Autowired
    public AuthFilter(JwtService jwtService, CustomerUserDetailsService customerUserDetailsService) {
        this.jwtService = jwtService;
        this.customerUserDetailsService = customerUserDetailsService;
    }

    private final String[] pathsToFilter = { "/code/auth" };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Filtering request for authentication");
        String token = getAccessToken(request);

        if (StringUtils.isEmpty(token) || !jwtService.verifyToken(token)) {
            log.error("Invalid token");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            DecodedJWT jwt = JWT.decode(token);

            if (jwt.getClaim("scope").isNull() || !checkClaimValue(token, authScope)) {
                log.error("Invalid token scope");
                filterChain.doFilter(request, response);
                return;
            }

            Authentication authUser = authenticateUser(jwt.getSubject());
            SecurityContextHolder.getContext().setAuthentication(authUser);

            log.info("Customer {} is authenticated", jwt.getSubject());

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Invalid token");
            filterChain.doFilter(request, response);
            return;
        }
    }

    /**
     * Attempts to retrieve the access token from the provided HttpServletRequest
     * object.
     * 
     * @param request the HttpServletRequest object from which to retrieve the
     *                access token
     * @return the access token value if found in the request cookies, or null if
     *         not found
     */
    private String getAccessToken(HttpServletRequest request) {

        log.info("Attempt to get access token");

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieTokenName)) {
                    log.info("Access token found");
                    return cookie.getValue();
                }
            }
        }

        log.info("No access token found");
        return null;
    }

    /**
     * Authenticates a user based on the provided username by loading the user
     * details
     * from the CustomerUserDetailsService.
     * 
     * @param username the username of the user to authenticate
     * @return an Authentication object representing the authenticated user
     */
    private Authentication authenticateUser(String username) {

        log.info("Attempt to authenticate the user {}", username);
        UserDetails user = customerUserDetailsService.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    private boolean checkClaimValue(String token, String value) {
        return jwtService.getScope(token).equals(value);
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
