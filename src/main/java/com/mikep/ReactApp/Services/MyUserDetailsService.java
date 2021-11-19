package com.mikep.ReactApp.Services;

import com.mikep.ReactApp.Models.Client;
import com.mikep.ReactApp.Models.Role;
import com.mikep.ReactApp.Models.User;
import com.mikep.ReactApp.Repositories.ClientRepository;
import com.mikep.ReactApp.Repositories.RoleRepository;
import com.mikep.ReactApp.Repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;

@Log4j2
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;

    public Client register(Client client, String siteURL) throws UnsupportedEncodingException,
            MessagingException {
        client.setVerificationCode(UUID.randomUUID().toString());
        client.setEnabled(false);
        client.setPassword(passwordEncoder.encode(client.getPassword()));
        client.setConfirmPassword("");
        client.setRoles(new HashSet(Arrays.asList(roleRepository.findByRoleName("ROLE_USER"))));
        client.setCreatedAt(Instant.now());
        Client savedClient = clientRepository.save(client);
        sendVerificationEmail(client, siteURL);
        return savedClient;
    }

    private void sendVerificationEmail(Client client, String siteURL) throws
            MessagingException, UnsupportedEncodingException {
        String verifyURL = siteURL + "/auth/verify?code=" + client.getVerificationCode();

        String toAddress = client.getUsername();
        String fromAddress = "mike.putnam@gmail.com";
        String senderName = "ReactApp";
        String subject = "Please verify your registration";
        String content = "Dear " + client.getName() + ",<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h2><a href=" + verifyURL + " target=\"_self\">Verify</a></h2>"
                + "Thank you,<br>"
                + "React App Team";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {
        Client client = clientRepository.findByVerificationCode(verificationCode);

        if (client == null || client.isEnabled()) {
            return false;
        } else {
            client.setVerificationCode(null);
            client.setEnabled(true);
            clientRepository.save(client);

            return true;
        }

    }
    @Override
    public UserDetails loadUserByUsername(String username) {
        Client user_model = clientRepository.findByUsername(username);
        if (user_model == null) {
            throw new UsernameNotFoundException(username);
        }
            //List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());

            return new org.springframework.security.core.userdetails.User(user_model.getUsername(),
                    user_model.getPassword(),
                    getUserAuthority(user_model.getRoles()));
    }

    public boolean isAdmin(String username) {
        Set<Role> roles = clientRepository.findByUsername(username).getRoles();
        for(Role role : roles) {
            if(role.getRoleName().contains("ROLE_ADMIN")) return true;
        }
        return false;
    }
    public List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach((role) -> {
            roles.add(new SimpleGrantedAuthority(role.getRoleName()));
        });

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }
}
