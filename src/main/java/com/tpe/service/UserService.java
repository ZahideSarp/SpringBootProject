package com.tpe.service;

import com.tpe.domain.Role;
import com.tpe.domain.User;
import com.tpe.domain.enums.UserRole;
import com.tpe.dto.UserRequest;
import com.tpe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    public void saveUser(UserRequest userRequest) {

        // DTO --> POJO
        User myUser = new User();

        myUser.setFirstName(userRequest.getFirstName());
        myUser.setLastName(userRequest.getLastName());
        myUser.setUserName(userRequest.getUserName());

        // password sifrelencek
        //Base64 ile clientten geldi  authendicationFilterda encode ettigim de plantext haline geldi
        //DB ye gondermek icin de passwordEncoder'ı tekrar kullanam gerekecek yani
        //bgrpt ile bu şifreyi hash'lemem lazım.
        String password = userRequest.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        myUser.setPassword(encodedPassword);

        // Role bilgisini setliyoruz, default degeri ADMIN olarak setliyoruz
        //
        Role role  =  roleService.getRoleType(UserRole.ROLE_ADMIN);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        myUser.setRole(roles);

        userRepository.save(myUser);

    }
}