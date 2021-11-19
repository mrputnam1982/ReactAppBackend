package com.mikep.ReactApp;

import com.mikep.ReactApp.Models.Client;
import com.mikep.ReactApp.Models.Privilege;
import com.mikep.ReactApp.Models.Role;
import com.mikep.ReactApp.Repositories.ClientRepository;
import com.mikep.ReactApp.Repositories.GuestRepository;
import com.mikep.ReactApp.Repositories.PrivilegeRepository;
import com.mikep.ReactApp.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
    boolean alreadySetup = false;

    @Value("${spring.data.admin_password}")
    private String password;

    @Value("${spring.data.admin_username}")
    private String username;

    @Value("${spring.data.guest_password}")
    private String guestPassword;

    @Value("${spring.data.guest_username}")
    private String guestUsername;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (alreadySetup)
            return;
        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        Privilege removePrivilege
                = createPrivilegeIfNotFound("REMOVE_PRIVILEGE");
        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege, removePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));
        createRoleIfNotFound("ROLE_GUEST", Arrays.asList(readPrivilege));
        if(clientRepository.findByUsername(guestUsername) == null) {
            Role guestRole = roleRepository.findByRoleName("ROLE_GUEST");
            Client client = new Client();
            client.setName("Guest");
            client.setEnabled(true);
            client.setPassword(passwordEncoder.encode(guestPassword));

            client.setUsername(guestUsername);
            client.setRoles(new HashSet(Arrays.asList(guestRole)));
            clientRepository.save(client);
        }
        if(clientRepository.findByUsername(username) == null) {
            Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN");
            Client client = new Client();
            client.setName("Admin");
            client.setEnabled(true);
            client.setPassword(passwordEncoder.encode(password));

            client.setUsername(username);
            client.setRoles(new HashSet(Arrays.asList(adminRole)));
            clientRepository.save(client);
        }
        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByPrivilegeName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByRoleName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}
