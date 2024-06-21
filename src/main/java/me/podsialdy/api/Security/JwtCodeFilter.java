package me.podsialdy.api.Security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.podsialdy.api.Service.JwtService;

/**
 * This class represents a filter for handling JWT authentication in the API.
 * It implements the Filter interface to intercept incoming requests and
 * validate JWT tokens.
 * If the token is missing, invalid, or does not have the required scope, it
 * returns an unauthorized response.
 * The filter extracts the JWT token from the request cookies and delegates
 * token verification to the JwtService.
 */
@Component
public class JwtCodeFilter implements Filter {

    @Value("${jwt.scope.pre_auth}")
    private String preAuthScope;

    private final JwtService jwtService;

    @Autowired
    public JwtCodeFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String token = getAccessToken(httpRequest);

        if (token == null || !jwtService.verifyToken(token) || !checkClaimValue(token, preAuthScope)) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }
        ;

        chain.doFilter(httpRequest, httpResponse);
    }

    private boolean checkClaimValue(String token, String value) {
        return jwtService.getScope(token).equals(value);
    }

    /**
     * Retrieves the access token from the provided HttpServletRequest object.
     * 
     * @param request the HttpServletRequest object containing the cookies
     * @return the access token value if found in the cookies, or null if not found
     */
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

}
