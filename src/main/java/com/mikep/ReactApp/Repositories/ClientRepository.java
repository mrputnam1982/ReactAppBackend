package com.mikep.ReactApp.Repositories;

import com.mikep.ReactApp.Event.ClientCascadeMongoEventListener;
import com.mikep.ReactApp.Models.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository extends MongoRepository<Client, String> {
    //@Query(value="{'id' : $0}", delete = true)
    //public Customer findById(String id);
    public void deleteById(String id);
    public Client findByUsername(String username);
    public Client findByVerificationCode(String verificationCode);

}
