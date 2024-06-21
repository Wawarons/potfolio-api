package me.podsialdy.api.Repository;

import java.util.UUID;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import me.podsialdy.api.Entity.Code;

public interface CodeRepository extends JpaRepository<Code, UUID>{

    List<Code> findFirstByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    
}