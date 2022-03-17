package com.mikep.ReactApp.Controllers;

import com.mikep.ReactApp.Models.AuthenticationResponse;
import com.mikep.ReactApp.Models.Image;
import com.mikep.ReactApp.Repositories.ClientRepository;
import com.mikep.ReactApp.Models.Client;
import com.mikep.ReactApp.Repositories.ImageRepository;
import com.mikep.ReactApp.Services.MyUserDetailsService;
import com.mikep.ReactApp.Utils.JwtUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@CrossOrigin(origins="http://localhost:8080")
@RequestMapping("/api")
@Log4j2
public class ClientsController {
    //private final Logger log = LogManager.getLogger(CustomersController.class);
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ImageRepository imageRepository;
    @GetMapping("/clients/getById/{id}")
    public Client getClient(@PathVariable String id) {
        return clientRepository.findById(id).get();
    }

    @GetMapping("/clients/getByUsername/{username}")
    public Client getClientByUsername(@PathVariable String username) {
        return clientRepository.findByUsername(username);
    }
    @GetMapping("/clients/getAll")
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity updateClient(@PathVariable String id, @RequestBody Client client) {
        Client currentClient = clientRepository.findById(id).get();
        if(client.getName() != null)
            currentClient.setName(client.getName());
        if(client.getUsername() != null)
            currentClient.setUsername(client.getUsername());
        if(client.getPassword() != null)
            currentClient.setPassword(client.getPassword());
        if(client.getRoles() != null)
            currentClient.setRoles(client.getRoles());
        if(client.getProfileHeading() != null)
            currentClient.setProfileHeading(client.getProfileHeading());
        if(client.getProfileInfo() != null)
            currentClient.setProfileInfo(client.getProfileInfo());
        if(client.getAvatar() != null) {
            if(imageRepository.findByUsername((client.getUsername())) != null)
                imageRepository.deleteByUsername(client.getUsername());

            imageRepository.save(client.getAvatar());
            currentClient.setAvatar(client.getAvatar());
        }
        currentClient = clientRepository.save(currentClient);

        return ResponseEntity.ok(currentClient);
    }

    @CrossOrigin(origins = {"http://localhost:8080", "http://localhost:3000"})
    @DeleteMapping("/clients/{id}")
    public ResponseEntity deleteClient(@PathVariable String id) {
        System.out.println(id);
        clientRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}