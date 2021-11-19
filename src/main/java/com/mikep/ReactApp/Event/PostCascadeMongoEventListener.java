package com.mikep.ReactApp.Event;

import com.mikep.ReactApp.Models.Client;
import com.mikep.ReactApp.Models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

public class PostCascadeMongoEventListener extends AbstractMongoEventListener<Post> {
    @Autowired
    private MongoOperations mongoOperations;
    private Post deletedPost;
    public @Override void onBeforeSave(BeforeSaveEvent<Post> event) {
        final Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(),
                new CascadeSaveCallback(source, mongoOperations));
    }
    public @Override void onBeforeDelete(BeforeDeleteEvent<Post> event) {
        final Object id = Objects.requireNonNull(event.getDocument()).get("_id");
        deletedPost = mongoOperations.findById(id, Post.class);
    }
    public @Override void onAfterDelete(AfterDeleteEvent<Post> event) {
        ReflectionUtils.doWithFields(Client.class,
                new CascadeDeleteCallback(deletedPost, mongoOperations));
    }
}
