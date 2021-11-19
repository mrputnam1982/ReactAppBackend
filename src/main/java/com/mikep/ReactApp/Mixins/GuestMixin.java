package com.mikep.ReactApp.Mixins;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
public abstract class GuestMixin
{
    @JsonProperty("id")
    private String getId;

    @JsonProperty("numArticlesPerMonth")
    private int getNumArticlesPerMonth;

    @JsonProperty("numArticlesReadThisMonth")
    private int getNumArticlesReadThisMonth;

    @JsonIgnore
    private Instant createdAt;
    @JsonIgnore
    private Instant updatedAt;
}
