package me.podsialdy.api.Entity;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Customer entity that implements the UserDetails interface.
 * This entity stores information about a customer, including their email,
 * username, password,
 * roles, block status, verification status, creation timestamp, and
 * authorities.
 * 
 * The Customer class provides methods to add roles, retrieve authorities,
 * password, and username,
 * as well as a toString method for displaying customer information.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(unique = true)
  @NotNull
  @Email
  private String email;

  @Column(unique = true)
  @NotNull
  private String username;
  
  @Size(min = 12, max = 250, message = "Password size must be greater between 12 and 250")
  @NotNull
  @Pattern(regexp = "(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*[0-9]+)(?=.*[.*$_!\\-+@;:/\\\\|']+)[A-Za-z0-9.*$_!\\-+@;:/\\\\|']+", message = "Password wrong format")
  private String password;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "customer_role", joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
  @Builder.Default
  private Set<Role> roles = new HashSet<>();

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "customer_refresh_token", joinColumns = @JoinColumn(name = "customer_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "refresh_token_id", referencedColumnName = "id"))
  @Builder.Default
  private Set<RefreshToken> refreshToken = new HashSet<>();

  @NotNull
  @Builder.Default
  private boolean isBlock = false;

  @NotNull
  @Builder.Default
  private boolean isVerified = false;

  @NotNull
  @Builder.Default
  private Instant createdAt = Instant.now();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
        .collect(Collectors.toList());
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void addRole(Role role) {
    roles.add(role);
  }

  public void removeRole(Role role) {
    roles.remove(role);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Customer))
      return false;
    Customer customer = (Customer) obj;
    return Objects.equals(id, customer.id);

  }

  public String toString() {
    return "Customer : \n\n email: " + email + "\n" +
        "username: " + username + "\n" +
        "id: " + id;
  }

}
