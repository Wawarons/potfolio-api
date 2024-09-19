package me.podsialdy.api.Security;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

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
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.podsialdy.api.Entity.Customer;
import me.podsialdy.api.Entity.RefreshToken;
import me.podsialdy.api.Repository.CustomerRepository;
import me.podsialdy.api.Repository.RefreshTokenRepository;
import me.podsialdy.api.Service.CookieService;
import me.podsialdy.api.Service.CustomerUserDetailsService;
import me.podsialdy.api.Service.JwtService;
import me.podsialdy.api.Utils.JwtConfig;

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

    @Value("${jwt.cookie.name}")
    private String cookieTokenName;

    @Value("${jwt.scope.auth}")
    private String authScope;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CookieService cookieService;


    @Autowired
    private JwtConfig jwtConfig;

    private final String[] pathsToNoFilter = { "/auth/" };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Filtering request for authentication");
        String token = getAccessToken(request);

        if (StringUtils.isEmpty(token)) {
            log.error("Token is empty");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {

            DecodedJWT jwt = JWT.decode(token);

            if (jwt.getClaim("session_id").isNull() || jwt.getClaim(jwtConfig.getClaimScope()).isNull()
                    || !jwt.getClaim(jwtConfig.getClaimScope()).asString().equals(authScope)) {
                log.error("Invalid token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            if (isTokenExpired(jwt)) {

                log.info("Token is expired");

                UUID sessionId = jwtService.getSession(token);

                Optional<RefreshToken> refreshToken = refreshTokenRepository
                        .findBySessionId(sessionId);

                log.info("Refresh token session: {}", sessionId);

                if (refreshToken.isEmpty() || refreshToken.get().isLocked()
                        || !refreshToken.get().getSessionId().equals(sessionId)) {
                    log.error("Refresh token not found or invalid");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                Optional<Customer> customer = customerRepository.findByUsername(jwt.getSubject());

                if (customer.isEmpty()) {
                    log.error("Customer not found for the subject {}", jwt.getSubject());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                token = jwtService.refreshToken(token);
                
                cookieService.addAccessToken(response, token);
                log.info("Update access token for customer {}", customer.get().getUsername());

                jwt = JWT.decode(token);

            }

            if (!jwtService.verifyToken(token)) {
                log.error("Invalid token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "invalid token");
                return;
            }
            ;

            Authentication authUser = authenticateUser(jwt.getSubject());
            SecurityContextHolder.getContext().setAuthentication(authUser);

            log.info("Customer {} is authenticated", jwt.getSubject());

            filterChain.doFilter(request, response);
        } catch (JWTDecodeException e) {
            log.error("Cannot decode the token: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot decode the token");
            return;
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An unexpected error occurred");
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

    /**
     * Check token's expiration 
     */
    private boolean isTokenExpired(DecodedJWT jwt) {
        return jwt.getExpiresAt().before(Date.from(Instant.now()));
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        for (String pathToNotFilter : pathsToNoFilter) {
            if (path.contains(pathToNotFilter)) {
                log.info("Path {} not filtered", path);
                return true;
            }
        }
        return false;
    }
}
