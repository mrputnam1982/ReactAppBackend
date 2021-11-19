package com.mikep.ReactApp.Configuration;

import com.mikep.ReactApp.Event.ClientCascadeMongoEventListener;
import com.mikep.ReactApp.Event.RoleCascadeMongoEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(MongoConfiguration.REPO_PACKAGE)
@Configuration
public class MongoConfiguration {
    static final String REPO_PACKAGE = "com.mikep.ReactApp.Repositories";
    public @Bean
    ClientCascadeMongoEventListener clientCascadeMongoEventListener() {
        return new ClientCascadeMongoEventListener();
    }

    public @Bean
    RoleCascadeMongoEventListener roleCascadeMongoEventListener() {
        return new RoleCascadeMongoEventListener();

    }
}
