package me.podsialdy.api.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import me.podsialdy.api.Entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
  
  //find role by role name
  Optional<Role> findByRole(String role);

}
