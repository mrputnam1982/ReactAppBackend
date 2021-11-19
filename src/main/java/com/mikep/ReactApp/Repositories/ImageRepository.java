package com.mikep.ReactApp.Repositories;

import com.mikep.ReactApp.Models.Client;
import com.mikep.ReactApp.Models.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<Image, String> {
    //@Query(value="{'id' : $0}", delete = true)
    //public Customer findById(String id);
    public void deleteById(String id);
    public void deleteByUsername(String username);
    public Image findByUsername(String username);
}
