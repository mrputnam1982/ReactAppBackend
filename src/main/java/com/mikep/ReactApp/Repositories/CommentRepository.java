package com.mikep.ReactApp.Repositories;

import com.mikep.ReactApp.Models.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {
    public void deleteById(String id);
}
