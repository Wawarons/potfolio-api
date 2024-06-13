package me.podsialdy.api.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.podsialdy.api.Entity.Role;
import me.podsialdy.api.Entity.RoleEnum;
import me.podsialdy.api.Repository.RoleRepository;

@Service
public class RoleService {

 @Autowired
 private RoleRepository roleRepository;

 /**
  * Initialize a role, check if a role already exist else create a new one.
  * @param newRole 
  * @return
  */
 public Role initRole(RoleEnum newRole) {
   Role role = roleRepository.findByRole(newRole.name()).orElse(null);
   if(role == null) {
     Role addRole = new Role();
     addRole.setRole(newRole.name());
     roleRepository.save(addRole);
     role = addRole;
   }

   return role;

 }

}
