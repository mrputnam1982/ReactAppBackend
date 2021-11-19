package com.mikep.ReactApp.Event;

import com.mikep.ReactApp.Models.Client;
import com.mikep.ReactApp.Models.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

public class CommentCascadeMongoEventListener extends AbstractMongoEventListener<Comment> {
    @Autowired
    private MongoOperations mongoOperations;
    private Comment deletedComment;
    public @Override void onBeforeSave(BeforeSaveEvent<Comment> event) {
        final Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(),
                new CascadeSaveCallback(source, mongoOperations));
    }
    public @Override void onBeforeDelete(BeforeDeleteEvent<Comment> event) {
        final Object id = Objects.requireNonNull(event.getDocument()).get("_id");
        deletedComment = mongoOperations.findById(id, Comment.class);
    }
    public @Override void onAfterDelete(AfterDeleteEvent<Comment> event) {
        ReflectionUtils.doWithFields(Client.class,
                new CascadeDeleteCallback(deletedComment, mongoOperations));
    }
}
