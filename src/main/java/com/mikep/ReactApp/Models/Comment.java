package com.mikep.ReactApp.Models;

import com.mikep.ReactApp.Annotations.Cascade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;

@Document(collection  = "Comments")
@Getter
@Setter
public class Comment {
  @Id
  private String id;

  private String postId;

  @NotNull(message= "Poster name cannot be null")
  @NotBlank(message = "Poster name cannot be blank")
  private String posterName;

  @NotNull(message= "Poster name cannot be null")
  @NotBlank(message = "Poster name cannot be blank")
  private String posterUsername;

  private String commentText;

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant modifiedAt;

  @DBRef
  @Cascade
  private List<Vote> votes;


  private HashMap<String, Boolean> usersVoted;
}
