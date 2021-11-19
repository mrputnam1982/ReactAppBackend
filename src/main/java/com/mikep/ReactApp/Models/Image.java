package com.mikep.ReactApp.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "Images")
public class Image {
    @Id
    private String id;

    private String username;

    String strBase64File;
}
