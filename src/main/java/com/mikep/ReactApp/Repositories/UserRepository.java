package com.mikep.ReactApp.Repositories;

import com.mikep.ReactApp.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    public User findByUsername(String username);
}
