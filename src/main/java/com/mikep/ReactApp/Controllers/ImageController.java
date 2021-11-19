package com.mikep.ReactApp.Controllers;

import com.mikep.ReactApp.Models.Image;
import com.mikep.ReactApp.Repositories.ImageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins="http://localhost:8080")
@RequestMapping("/api")
@Log4j2
public class ImageController {

    @Autowired
    ImageRepository imageRepository;

    @GetMapping("/getImage/{username}")
    public ResponseEntity<Image> getImage(@PathVariable String username) {
            Image image = imageRepository.findByUsername(username);
            return new ResponseEntity<>(image, HttpStatus.OK);
    }
}