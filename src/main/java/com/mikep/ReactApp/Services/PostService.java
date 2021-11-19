package com.mikep.ReactApp.Services;

import com.mikep.ReactApp.Models.Comment;
import com.mikep.ReactApp.Models.Post;
import com.mikep.ReactApp.Repositories.CommentRepository;
import com.mikep.ReactApp.Repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public Post saveOrUpdatePost(Post post) {
        return postRepository.save(post);
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    public Post getPost(String id) {
        return postRepository.findById(id).get();
    }

    public void deletePost(String id ) { postRepository.deleteById(id);}

    public Comment saveOrUpdateComment(Comment comment) { return commentRepository.save(comment); }

    public void deleteComment(String id) { commentRepository.deleteById(id); }


}
