package com.mikep.ReactApp.Repositories;

import com.mikep.ReactApp.Models.Privilege;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrivilegeRepository extends MongoRepository<Privilege, String> {
    public Privilege findByPrivilegeName(String privilegeName);
}
