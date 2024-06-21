package me.podsialdy.api.Entity;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Role entity that is mapped to the database table 'Role'.
 * Includes properties for id, role, and createdAt.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(unique = true)
    private String role;

    @NotNull
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Role))
            return false;
        
        Role role = (Role) obj;
        
        return Objects.equals(this.id, role.id)
            && Objects.equals(this.role, role.role)
            && Objects.equals(this.createdAt, role.createdAt);

    }

    public String toString() {
        return "Role: " + this.role;
    }

}
