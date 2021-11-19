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

  @NotNull(message = "Comment cannot be blank.")
  @NotBlank(message = "Comment cannot be blank.")
  private String commentText;

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant modifiedAt;

}
