package me.podsialdy.api.Service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CookieService {

    public void addAccesstoken(HttpServletResponse response, String token) {

        log.info("Attempt to add cookie access token");
        Cookie accessTokenCookie = new Cookie("token", token);
        accessTokenCookie.setHttpOnly(true);
        // TODO Set up to true for production
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        // TODO Check if -1 is for session
        // accessTokenCookie.setMaxAge(-1);
        response.addCookie(accessTokenCookie);
        log.info("Cookie access token added");
    }

    public void removeAccessToken(HttpServletResponse response) {
        log.info("Attempt to delete cookie access token");
        Cookie accessTokenCookie = new Cookie("token", null);
        accessTokenCookie.setHttpOnly(true);
        // TODO Set up to true for production
        accessTokenCookie.setSecure(false);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        response.addCookie(accessTokenCookie);
        log.info("Cookie access token deleted");
    }

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
