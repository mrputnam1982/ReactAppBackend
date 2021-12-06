package com.mikep.ReactApp.Repositories;

import com.mikep.ReactApp.Models.Comment;
import com.mikep.ReactApp.Models.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VoteRepository extends MongoRepository<Vote, String> {
    public void deleteById(String id);
    public Vote findByUsernameAndCommentId(String username, String commentId);
}