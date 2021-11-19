package com.mikep.ReactApp.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Set;
@JsonIgnoreProperties(ignoreUnknown=true)
@Setter
@Getter
@Document(collection="Guests")
public class Guest {
    @Id
    private String id;

    private int numArticlesPerMonth;

    private int numArticlesReadThisMonth;


    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;


}
