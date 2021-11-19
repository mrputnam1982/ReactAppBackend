package com.mikep.ReactApp.Controllers;

import com.mikep.ReactApp.Exceptions.*;
import com.mikep.ReactApp.Models.*;
import com.mikep.ReactApp.Repositories.ClientRepository;
import com.mikep.ReactApp.Repositories.GuestRepository;
import com.mikep.ReactApp.Repositories.UserRepository;
import com.mikep.ReactApp.Services.MapValidationService;
import com.mikep.ReactApp.Services.MyUserDetailsService;
import com.mikep.ReactApp.Utils.GuestToJson;
import com.mikep.ReactApp.Utils.JwtUtil;
import com.mikep.ReactApp.Validators.UserValidator;
import lombok.extern.log4j.Log4j2;
import net.minidev.json.JSONObject;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

@RestController
@CrossOrigin(origins="http://localhost:8080")
@RequestMapping("/auth/")
@Log4j2
public class AuthController {

    @Value("${spring.data.guest_username}")
    private String guestUsername;

    @Value("${spring.data.guest_password}")
    private String guestPassword;

    @Value("${spring.data.refresh_token_key}")
    private String refreshTokenKey;


    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    MapValidationService mapValidationService;

    Key aesKey;


    @PostMapping("guest")
    public ResponseEntity<Cookie> createGuestAndGuestAuthenticationCookie(@RequestBody Guest guest)
        throws Exception {
        GuestToJson obj = new GuestToJson();
        List<String> entities = new ArrayList<>();
        guest.setCreatedAt(Instant.now());
        Guest newGuest = guestRepository.save(guest);

        Guest updatedGuest = guestRepository.findByCreatedAt(newGuest.getCreatedAt());
        log.info(updatedGuest);
        String jsonGuest = obj.objToJson(updatedGuest);
        log.info(jsonGuest);
        entities.add(jsonGuest);

        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    guestUsername,
                    guestPassword));
        } catch(BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(guestUsername);

        final String jwt = jwtTokenUtil.generateToken(userDetails);
        log.info(jwt);
        entities.add("jwt: " + jwt);
        Cookie cookie = new Cookie("guestCookie", String.join(",", entities));
        cookie.setHttpOnly(true);
        //return ResponseEntity.ok(new AuthenticationResponse(jwt));
        return new ResponseEntity<>(cookie, HttpStatus.OK);

    }

    @GetMapping("guest")
    public ResponseEntity<List<String>> getGuestCookieContents(@CookieValue(name="guestCookie") String cookieValue) {

        return new ResponseEntity<>
                (Arrays.asList(cookieValue.split(",",-1)),HttpStatus.OK);
    }
    @PutMapping("login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody Client client)
            throws Exception {
        //log.info(client.getUsername());
        Client savedClient = clientRepository.findByUsername(client.getUsername());
        if(savedClient == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if(savedClient.isEnabled() == false)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    client.getUsername(),
                    client.getPassword()));
        } catch(BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(savedClient.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);
        log.info(jwt);
        AuthenticationResponse authResponse = new AuthenticationResponse();
        authResponse.setUsername(savedClient.getName());
        authResponse.setJwt(jwt);
        authResponse.setRoles(savedClient.getRoles());
        authResponse.setAvatar(savedClient.getAvatar());
        //return ResponseEntity.ok(new AuthenticationResponse(jwt));
        return new ResponseEntity<>(authResponse, HttpStatus.OK);


    }

    @PostMapping("refresh_token/generate")
    public ResponseEntity<Cookie> createRefreshToken(@RequestBody String username) {

        try {
            log.info(username);
            aesKey = new SecretKeySpec(refreshTokenKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(username.getBytes());
            Cookie refresh_token = new Cookie("refresh_token",
                    Base64.getEncoder().encodeToString(encrypted));
            refresh_token.setMaxAge(24 * 60 * 60);
            refresh_token.setHttpOnly(true);
            refresh_token.setSecure(true);
            return new ResponseEntity<>(refresh_token, HttpStatus.OK);
        } catch(NoSuchAlgorithmException |
                NoSuchPaddingException |
                IllegalBlockSizeException |
                BadPaddingException |
                InvalidKeyException e) {
            throw new CustomInvalidKeyException("Encryption of username with key "
                    + aesKey
                    + " failed");
        }
    }

    @PostMapping("refresh_token/update")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody CustomCookie cookie) {
        try {


            log.info(cookie);
            String username = cookie.getUsername();

            String cookieValue = cookie.getCookieValue();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            String decryptedVal = new String(cipher.doFinal(
                    Base64.getDecoder().decode(cookieValue)));
            if (decryptedVal.equals(username)) {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                Client client = clientRepository.findByUsername(userDetails.getUsername());
                Set<Role> roles = client.getRoles();
                Image avatar = client.getAvatar();
                final String jwt = jwtTokenUtil.generateToken(userDetails);
                log.info("Created new User JWT token");
                return new ResponseEntity<>(new AuthenticationResponse(jwt,
                        userDetails.getUsername(),
                        roles,
                        avatar), HttpStatus.OK);
            }

            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        } catch(NoSuchAlgorithmException |
                NoSuchPaddingException |
                IllegalBlockSizeException |
                BadPaddingException |
                InvalidKeyException e) {
            throw new CustomInvalidKeyException("Encryption of username with key "
                    + aesKey
                    + " failed");
        }
    }
    @PostMapping("register")
    public ResponseEntity<?> createUser(@Valid @RequestBody Client client,
                                        BindingResult result,
                                        HttpServletRequest request)
            throws URISyntaxException {
        //log.debug(client.toString());

        Client temp = new Client();
        temp = client;
        userValidator.validate(temp, result);
        ResponseEntity<?> errorMap = mapValidationService.MapValidationSvc(result);
        if(errorMap != null) return errorMap;

        try{

            //client.setUsername(client.getUsername());

            Client savedClient = userDetailsService.register(temp, getSiteURL(request));

            return new ResponseEntity<Client>(savedClient,HttpStatus.CREATED);
        }
        catch(Exception e) {
            log.info(e.getMessage());
            if(e.getClass() == UsernameExistsException.class)
                throw new UsernameExistsException("Username: " + client.getUsername() + " already exists");
            else
                return new ResponseEntity<String>("Registration failed", HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (userDetailsService.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }
    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
