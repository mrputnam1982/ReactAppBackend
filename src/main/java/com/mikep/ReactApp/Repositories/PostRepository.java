package com.mikep.ReactApp.Repositories;

import com.mikep.ReactApp.Models.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Post, String> {
    public void deleteById(String id);
}
