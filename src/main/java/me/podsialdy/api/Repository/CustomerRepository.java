package me.podsialdy.api.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import me.podsialdy.api.Entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {

  //find user by email 
  Optional<Customer> findByEmail(String email);

  //find user by username
  Optional<Customer> findByUsername(String username);

}
