package com.mikep.ReactApp.Repositories;

import com.mikep.ReactApp.Models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
    Role findByRoleName(String roleName);
}
