package com.mikep.ReactApp.Controllers;

import com.mikep.ReactApp.Models.Comment;
import com.mikep.ReactApp.Models.Image;
import com.mikep.ReactApp.Models.Post;
import com.mikep.ReactApp.Models.Vote;
import com.mikep.ReactApp.Repositories.CommentRepository;
import com.mikep.ReactApp.Repositories.ImageRepository;
import com.mikep.ReactApp.Repositories.VoteRepository;
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
import java.util.Optional;

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
    private CommentRepository commentRepository;

    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/posts")
    public ResponseEntity<?> createNewPost(HttpServletRequest request,
                                              @RequestBody Post post) {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = jwtUtil.getUsernameFromAuthHeader(authorizationHeader);
        if(username != null) {
            post.setModifiedAt(Instant.now());
            post = postService.saveOrUpdatePost(post);
            return new ResponseEntity<Post>(post, HttpStatus.CREATED);
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
                    old_post.setModifiedAt(Instant.now());
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
//    @PutMapping("/posts/comments/votes/{id}")
//    public ResponseEntity<Comment> updateCommentVotes(
//            @PathVariable String id,
//            @RequestBody Comment comment ) {
//        Comment existingComment = postService.getComment(id);
//        existingComment.setUsersVoted(comment.getUsersVoted());
//        existingComment.setUpVotes(comment.getUpVotes());
//        existingComment.setDownVotes(comment.getDownVotes());
//
//        int index = 0;
//        for (Comment c : commentRepository.findAll()) {
//            if (c.getId() == comment.getId()) {
//                old_post.getComments().remove(index);
//                old_post.getComments().add(newComment);
//                break;
//            }
//            index++;
//        }
//        Comment returnComment = postService.saveOrUpdateComment(existingComment);
//        return new ResponseEntity<> (returnComment, HttpStatus.OK);
//    }
    @PostMapping("/posts/comments/{id}")
    public ResponseEntity<Comment> createNewComment(
            HttpServletRequest request,
            @PathVariable String id,
            @RequestBody Comment comment) {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = jwtUtil.getUsernameFromAuthHeader(authorizationHeader);

        Post old_post = postService.getPost(id);
        Comment newComment = null;
        if(old_post != null) {
            Instant previousModifiedAt = old_post.getModifiedAt();
            if(old_post.getComments() != null) log.info(old_post.getComments());
            if(comment.getId() != null) {
                Comment oldComment = commentRepository.findById(comment.getId()).get();

                newComment = postService.saveOrUpdateComment(oldComment);
                if (oldComment != null) {
                    //add or revise the new vote
                    //voteRepository.saveAll(comment.getVotes());
                    Vote existingVote = voteRepository.findByUsernameAndCommentId(
                            username, comment.getId());
                    List<Vote> newVotes = new ArrayList<>();
                    if(existingVote == null) {
                        //no id yet for this vote, find in comment request and save it
                        //to the vote repository
                            for (Vote vote : comment.getVotes()) {
                                if (vote.getUsername().contains(username)) {
                                    existingVote = voteRepository.save(vote);
                                    newVotes.add(existingVote);

                                } else newVotes.add(vote);

                            }
                            oldComment.setVotes(newVotes);
                    }

                    else {
                        for(Vote vote: comment.getVotes()) {
                            if (vote.getUsername().contains(username)) {
                                existingVote.setVoteType(vote.getVoteType());
                                Vote modifiedVote = voteRepository.save(existingVote);
                                newVotes.add(modifiedVote);
                            }
                            else newVotes.add(vote);
                        }
                        oldComment.setVotes(newVotes);
                    }
                    oldComment.setPostId(id);
                    //oldComment.setUsersVoted(comment.getUsersVoted());
                    newComment = postService.saveOrUpdateComment(oldComment);

                    int index = 0;
                    for (Comment c : commentRepository.findAll()) {
                        if (c.getId() == comment.getId()) {
                            old_post.getComments().remove(index);
                            old_post.getComments().add(newComment);
                            break;
                        }
                        index++;
                    }
                }
            }
            else {
                comment.setCreatedAt(Instant.now());

                newComment = postService.saveOrUpdateComment(comment);
                if (old_post.getComments() == null) {
                    List<Comment> comments = new ArrayList<>();
                    comments.add(newComment);
                    old_post.setComments(comments);
                } else old_post.getComments().add(newComment);
            }
            //don't update post's modified at time if this is a comment update??
            old_post.setModifiedAt(previousModifiedAt);
            postService.saveOrUpdatePost(old_post);

            return new ResponseEntity<Comment>(newComment, HttpStatus.OK);
        }
        else return new ResponseEntity<Comment>(newComment, HttpStatus.BAD_REQUEST);

    }
}
