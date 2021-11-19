package com.mikep.ReactApp.Repositories;

import com.mikep.ReactApp.Models.Client;
import com.mikep.ReactApp.Models.Guest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;

public interface GuestRepository extends MongoRepository<Guest, String> {
    //@Query(value="{'id' : $0}", delete = true)
    public Guest findByCreatedAt(Instant createdAt);
    public void deleteById(String id);

}
