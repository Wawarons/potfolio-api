package me.podsialdy.api.Entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

/**
 * RefreshTokenTest
 */
@ActiveProfiles("test")
public class RefreshTokenTest {

        @Test
        public void test_refreshToken_setter() {

                UUID id = UUID.randomUUID();
                UUID sessionID = UUID.randomUUID();
                Customer customer = mock(Customer.class);
                Instant now = Instant.now();
                String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflK";
                RefreshToken refreshToken = new RefreshToken();

                refreshToken.setId(id);
                refreshToken.setToken(token);
                refreshToken.setCustomer(customer);
                refreshToken.setSessionId(sessionID);
                refreshToken.setLocked(false);
                refreshToken.setCreatedAt(now);

                assertEquals(id, refreshToken.getId());
                assertEquals(sessionID, refreshToken.getSessionId());
                assertEquals(customer, refreshToken.getCustomer());
                assertEquals(now, refreshToken.getCreatedAt());
                assertEquals(token, refreshToken.getToken());
                assertEquals(false, refreshToken.isLocked());

        }

        @Test
        public void test_refreshToken_builder() {
                
                UUID idValue = UUID.randomUUID();
                UUID sessionID = UUID.randomUUID();
                Customer customerValue = mock(Customer.class);
                Instant now = Instant.now();
                Instant expirationValue = Instant.now().plus(5L, ChronoUnit.MINUTES);
                String tokenValue = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflK";
                
                RefreshToken refreshToken = RefreshToken.builder()
                .id(idValue)
                .sessionId(sessionID)
                .customer(customerValue)
                .createdAt(now)
                .expiration(expirationValue)
                .token(tokenValue)
                .isLocked(false)
                .build();

                assertEquals(idValue, refreshToken.getId());
                assertEquals(sessionID, refreshToken.getSessionId());
                assertEquals(customerValue, refreshToken.getCustomer());
                assertEquals(now, refreshToken.getCreatedAt());
                assertEquals(tokenValue, refreshToken.getToken());
                assertEquals(expirationValue, refreshToken.getExpiration());
                assertEquals(false, refreshToken.isLocked());


        }

        @Test
        public void test_refreshToken_toString() {

                Customer customer = mock(Customer.class);
                Instant expiration = Instant.now().plus(30L, ChronoUnit.DAYS);
                RefreshToken refreshToken = new RefreshToken(UUID.randomUUID(),
                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflK",
                                customer, UUID.randomUUID(), expiration, false, Instant.now());

                String expected = "RefreshToken: \n\n  id: " + refreshToken.getId() +
                                "\n sessionId: " + refreshToken.getSessionId() +
                                "\n token: " + refreshToken.getToken() +
                                "\n expiration: " + refreshToken.getExpiration() +
                                "\n isLocked: " + refreshToken.isLocked() +
                                "\n createdAt: " + refreshToken.getCreatedAt();

                assertEquals(expected, refreshToken.toString());
        }

        @Test
        public void test_refreshToken_equals() {
                Customer customer = mock(Customer.class);
                Instant expiration = Instant.now().plus(30L, ChronoUnit.DAYS);
                RefreshToken refreshToken = new RefreshToken(UUID.randomUUID(),
                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflK",
                                customer, UUID.randomUUID(), expiration, false, Instant.now());

                RefreshToken refreshToken2 = new RefreshToken(UUID.randomUUID(),
                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflK",
                                customer, UUID.randomUUID(), expiration, false, Instant.now());

                RefreshToken refreshToken3 = new RefreshToken(refreshToken.getId(),
                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflK",
                                customer, UUID.randomUUID(), expiration, false, Instant.now());

                assertFalse(refreshToken.equals(refreshToken2));
                assertTrue(refreshToken.equals(refreshToken3));
                assertTrue(refreshToken.equals(refreshToken));
                assertFalse(refreshToken.equals(customer));
                assertFalse(refreshToken.equals(null));

                
        }

        @Test
        public void test_refreshToken_hashcode() {
                UUID id = UUID.randomUUID();
                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setId(id);

                assertEquals(id.hashCode(), refreshToken.hashCode());

                refreshToken.setId(null);
                assertEquals(0, refreshToken.hashCode());
        }

}