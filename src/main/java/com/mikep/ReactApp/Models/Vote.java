package com.mikep.ReactApp.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Votes")
@Getter
@Setter
public class Vote {

    enum TYPE_VOTE {
        UP,
        DOWN
    };

    @Id
    private String id;
    private String postId;
    private String commentId;
    private String username;
    private TYPE_VOTE voteType;
}
