package com.mikep.ReactApp.Event;

import com.mikep.ReactApp.Models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

public class ClientCascadeMongoEventListener extends AbstractMongoEventListener<Client> {
    @Autowired
    private MongoOperations mongoOperations;
    private Client deletedClient;
    public @Override void onBeforeSave(BeforeSaveEvent<Client> event) {
        final Object source = event.getSource();
        ReflectionUtils.doWithFields(source.getClass(),
                new CascadeSaveCallback(source, mongoOperations));
    }
    public @Override void onBeforeDelete(BeforeDeleteEvent<Client> event) {
        final Object id = Objects.requireNonNull(event.getDocument()).get("_id");
        deletedClient = mongoOperations.findById(id, Client.class);
    }
    public @Override void onAfterDelete(AfterDeleteEvent<Client> event) {
        ReflectionUtils.doWithFields(Client.class,
                new CascadeDeleteCallback(deletedClient, mongoOperations));
    }
}
