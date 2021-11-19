package com.mikep.ReactApp.Models;


import com.mikep.ReactApp.Annotations.Cascade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

@Document(collection  = "Posts")
@Getter
@Setter
public class Post {
    @Id
    private String id;

    //    @NotNull(message = "Please enter an id for this post")
    //    @NotBlank(message = "Please enter an id for this post")
    //    private String postIdentifier;

    @NotNull(message = "Please enter a title for this post")
    @NotBlank(message = "Please enter a title for this post")
    private String title;

    @NotNull(message = "Please enter the body text for this post")
    @NotBlank(message = "Please enter the body text for this post")
    private String body;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    @DBRef
    @Cascade()
    private List<Comment> comments;
}
