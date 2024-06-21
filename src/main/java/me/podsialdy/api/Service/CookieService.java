package me.podsialdy.api.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class for managing cookies related to access tokens.
 * Provides methods to add, remove, and retrieve access tokens stored in
 * cookies.
 */
@Service
@Slf4j
public class CookieService {

    @Value("${jwt.cookie.name}")
    private String cookieName;

    @Value("${jwt.cookie.secure}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.path}")
    private String cookiePath;

    /**
     * Adds an access token to the response as a cookie.
     * 
     * @param response the HttpServletResponse object to which the cookie will be
     *                 added
     * @param token    the access token to be stored in the cookie
     */
    public void addAccesstoken(HttpServletResponse response, String token) {

        log.info("Attempt to add cookie access token");
        Cookie accessTokenCookie = new Cookie(cookieName, token);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(cookieSecure);
        accessTokenCookie.setPath(cookiePath);
        response.addCookie(accessTokenCookie);
        log.info("Cookie access token added");
    }

    /**
     * Removes the access token cookie from the HttpServletResponse.
     * 
     * @param response the HttpServletResponse object from which the cookie will be
     *                 removed
     */
    public void removeAccessToken(HttpServletResponse response) {
        log.info("Attempt to delete cookie access token");
        Cookie accessTokenCookie = new Cookie("token", null);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);
        log.info("Cookie access token deleted");
    }

    /**
     * Retrieves the access token from the provided HttpServletRequest object.
     * 
     * @param request the HttpServletRequest object containing the cookies
     * @return a String representing the access token if found in cookies, or an
     *         empty string if not found
     */
    public String getAccessToken(HttpServletRequest request) {

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
