package me.podsialdy.api.Service;

import jakarta.servlet.http.Cookie;
import me.podsialdy.api.Utils.CookieConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
public class CookieServiceTest {

    @Mock
    private CookieConfig cookieConfig;

    @InjectMocks
    private CookieService cookieService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_CookieService_add_access_token() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "fake-token";

        when(cookieConfig.getCookieName()).thenReturn("name");
        when(cookieConfig.isCookieSecure()).thenReturn(true);
        when(cookieConfig.getCookiePath()).thenReturn("/");

        cookieService.addAccessToken(response, token);

        assertNotNull(response.getCookie(cookieConfig.getCookieName()));
        assertTrue(response.getCookie(cookieConfig.getCookieName()).isHttpOnly());
        assertEquals(response.getCookie(cookieConfig.getCookieName()).getPath(), "/");
        assertEquals(response.getCookie(cookieConfig.getCookieName()).getName(), "name");
        assertEquals(response.getCookie(cookieConfig.getCookieName()).getValue(), "fake-token");
    }

    @Test
    public void test_CookieService_remove_access_token() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "fake-token";

        when(cookieConfig.getCookieName()).thenReturn("name");
        when(cookieConfig.isCookieSecure()).thenReturn(true);
        when(cookieConfig.getCookiePath()).thenReturn("/");

        cookieService.removeAccessToken(response);

        assertEquals(response.getCookie(cookieConfig.getCookieName()).getName(), "name");
        assertEquals(response.getCookie(cookieConfig.getCookieName()).getMaxAge(), 0);
    }

    @Test
    public void test_CookieService_get_access_token_null() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String result = cookieService.getAccessToken(request);

        assertNull(result);
    }

    @Test
    public void test_CookieService_get_access_token() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie accessTokenCookie = new Cookie("name", "fake-token");
        when(cookieConfig.getCookieName()).thenReturn("name");

        request.setCookies(accessTokenCookie);

        String result = cookieService.getAccessToken(request);

        assertEquals(result, "fake-token");
    }

    @Test
    public void test_CookieService_get_access_token_not_found() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie accessTokenCookie = new Cookie("wrong_name", "fake-token");
        when(cookieConfig.getCookieName()).thenReturn("name");

        request.setCookies(accessTokenCookie);

        String result = cookieService.getAccessToken(request);

        assertNull(result);
    }

}
