package com.mikep.ReactApp.Models;

import com.mikep.ReactApp.Annotations.Cascade;
import com.mikep.ReactApp.Annotations.ValidPassword;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;

@Document(collection  = "Clients")
@Getter
@Setter
public class Client {
    @Id
    private String id;

    @NotNull(message = "Please enter your full name")
    @NotBlank(message = "Please enter your full name")
    private String name;
    @NotNull(message = "Please enter an email")
    @NotBlank(message = "Please enter an email")
    @Email(message = "Please enter a valid email")
    @Indexed(unique = true)
    private String username;
    @ValidPassword
    @NotNull(message = "Please enter a password")
    @NotBlank(message = "Please enter a password")
    private String password;
    @Transient
    private String confirmPassword;

    private String profileHeading;

    private String profileInfo;

    @DBRef
    @Cascade()
    private Image avatar;

    private String verificationCode;
    private boolean enabled;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    @DBRef
    @Cascade()
    private Set<Role> roles;

//    @PrePersist
//    protected void onPrePersist() {
//        this.createdAt = new Date();
//    }
//    @PreUpdate
//    protected void onPreUpdate() {
//        this.updatedAt = new Date();
//    }
//    public Client(String id, String name, String email, String pw) {
//        this.id = id;
//        this.name = name;
//        this.email = email;
//        this.password = pw;
//    }

//    @Override
//    public String toString() {
//        return new String("Client: id: " + this.id + ", name: " + this.name +
//                ", email: " + this.username);
//    }
}
