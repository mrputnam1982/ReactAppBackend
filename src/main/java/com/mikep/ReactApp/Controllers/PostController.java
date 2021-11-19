package com.mikep.ReactApp.Controllers;

import com.mikep.ReactApp.Models.Comment;
import com.mikep.ReactApp.Models.Image;
import com.mikep.ReactApp.Models.Post;
import com.mikep.ReactApp.Repositories.ImageRepository;
import com.mikep.ReactApp.Services.MyUserDetailsService;
import com.mikep.ReactApp.Services.PostService;
import com.mikep.ReactApp.Utils.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@CrossOrigin(origins="http://localhost:8080")
@RequestMapping("/api")
@Log4j2
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private ImageRepository imageRepository;


    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/posts")
    public ResponseEntity<?> createNewPost(HttpServletRequest request,
                                              @RequestBody Post post) {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = jwtUtil.getUsernameFromAuthHeader(authorizationHeader);
        if(username != null) {
            if (myUserDetailsService.isAdmin(username)) {
                post = postService.saveOrUpdatePost(post);
                return new ResponseEntity<Post>(post, HttpStatus.CREATED);

            } else return new ResponseEntity<String>(
                    "Insufficient Privileges", HttpStatus.BAD_REQUEST);
        }
        else return new ResponseEntity<String>("Username invalid", HttpStatus.BAD_REQUEST);


    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<?> editPost(HttpServletRequest request,
                                         @PathVariable String id,
                                         @RequestBody Post post) {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = jwtUtil.getUsernameFromAuthHeader(authorizationHeader);
        if(username != null) {
            if (myUserDetailsService.isAdmin(username)) {
                Post old_post = postService.getPost(id);
                if(old_post != null) {
                    old_post.setTitle(post.getTitle());
                    old_post.setBody(post.getBody());

                    postService.saveOrUpdatePost(old_post);
                    return new ResponseEntity<Post>(old_post, HttpStatus.OK);
                }
                else return new ResponseEntity<String>("Post empty", HttpStatus.BAD_REQUEST);
            } else return new ResponseEntity<String>(
                    "Insufficient Privileges", HttpStatus.BAD_REQUEST);
        }
        else return new ResponseEntity<String>("Username invalid", HttpStatus.BAD_REQUEST);


    }
    @GetMapping("/posts")
    public List<Post> getPosts() { return postService.getPosts(); }

    @GetMapping("/posts/{id}")
    public Post getPost(@PathVariable String id) {
        return postService.getPost(id);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<String> deletePost(HttpServletRequest request, @PathVariable String id) {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = jwtUtil.getUsernameFromAuthHeader(authorizationHeader);
        if(username != null) {
           if (myUserDetailsService.isAdmin(username)) {
                postService.deletePost(id);
                return new ResponseEntity<String>("Post deleted successfully", HttpStatus.OK);

            } else return new ResponseEntity<String>(
                    "Insufficient Privileges", HttpStatus.BAD_REQUEST);
        }
        else return new ResponseEntity<String>("Username invalid", HttpStatus.BAD_REQUEST);

    }
    @DeleteMapping("/posts/comments/{id}")
    public ResponseEntity<String> deleteComment(HttpServletRequest request, @PathVariable String id) {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = jwtUtil.getUsernameFromAuthHeader(authorizationHeader);
        if(username != null) {
            if (myUserDetailsService.isAdmin(username)) {
                postService.deleteComment(id);
                return new ResponseEntity<String>("Comment deleted successfully", HttpStatus.OK);

            } else return new ResponseEntity<String>(
                    "Insufficient Privileges", HttpStatus.BAD_REQUEST);
        }
        else return new ResponseEntity<String>("Username invalid", HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/posts/comments/{id}")
    public ResponseEntity<Comment> createNewComment(
            @PathVariable String id,
            @RequestBody Comment comment) {
        Post old_post = postService.getPost(id);
        Comment newComment = null;
        if(old_post != null) {

            if(old_post.getComments() != null) log.info(old_post.getComments());
            comment.setCreatedAt(Instant.now());
            newComment = postService.saveOrUpdateComment(comment);
            if(old_post.getComments() == null) {
                List<Comment> comments = new ArrayList<>();
                comments.add(newComment);
                old_post.setComments(comments);
            }
            else old_post.getComments().add(newComment);
            postService.saveOrUpdatePost(old_post);

            return new ResponseEntity<Comment>(newComment, HttpStatus.OK);
        }
        else return new ResponseEntity<Comment>(newComment, HttpStatus.BAD_REQUEST);

    }
}
