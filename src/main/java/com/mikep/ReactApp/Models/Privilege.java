package com.mikep.ReactApp.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;
@Document(collection = "Privileges")
public class Privilege {

    public Privilege(String privilegeName) {
        this.privilegeName = privilegeName;
    }
    @Id
    private String id;

    private String privilegeName;
}
