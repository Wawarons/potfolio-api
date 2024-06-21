package me.podsialdy.api.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.podsialdy.api.Entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  //find user by email 
  Optional<Customer> findByEmail(String email);

  //find user by username
  Optional<Customer> findByUsername(String username);

  @Query("SELECT c FROM Customer c WHERE c.username = :value OR c.email = :value")
  Optional<Customer> findByUsernameOrEmail(String value);

}
