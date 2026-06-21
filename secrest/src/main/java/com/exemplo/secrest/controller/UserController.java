package com.exemplo.secrest.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemplo.secrest.dto.CreateUserDto;
import com.exemplo.secrest.dto.EmailDto;
import com.exemplo.secrest.dto.LoginUserDto;
import com.exemplo.secrest.dto.RecoveryJwtTokenDto;
import com.exemplo.secrest.dto.RequestCodeDto;
import com.exemplo.secrest.dto.UpdateProfileDto;
import com.exemplo.secrest.dto.UserProfileDto;
import com.exemplo.secrest.entity.Role;
import com.exemplo.secrest.entity.User;
import com.exemplo.secrest.enums.RoleName;
import com.exemplo.secrest.producer.UserProducer;
import com.exemplo.secrest.repository.UserRepository;
import com.exemplo.secrest.service.CodigoCacheService;
import com.exemplo.secrest.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CodigoCacheService codigoCacheService;

    @Autowired
    private UserProducer userProducer;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody CreateUserDto dto) {
        userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/users")
    public ResponseEntity<String> getProtectedInfo(Authentication authentication) {
        return ResponseEntity.ok("Acesso permitido para: " + authentication.getName());
}

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> login(@RequestBody LoginUserDto dto) {
        RecoveryJwtTokenDto token = userService.authenticateUser(dto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Autenticado com sucesso!");
    }

    @GetMapping("/test/customer")
    public ResponseEntity<String> customerTest() {
        return ResponseEntity.ok("Acesso de CUSTOMER autorizado!");
    }

    @GetMapping("/test/administrator")
    public ResponseEntity<String> adminTest() {
        return ResponseEntity.ok("Acesso de ADMINISTRATOR autorizado!");
    }

    @PostMapping("/auth/request-code")
    public ResponseEntity<Void> requestCode(@RequestBody RequestCodeDto dto) {

        User user = userRepository.findByEmail(dto.email()).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(dto.email());

            newUser.setRoles(List.of(Role.builder().name(RoleName.ROLE_CUSTOMER).build()));
            newUser.setPassword(UUID.randomUUID().toString());
            return userRepository.save(newUser);
        });

        String code = codigoCacheService.generateAndSaveCode(user.getEmail());

        EmailDto emailDto = new EmailDto(
                user.getId(),
                user.getEmail(),
                "Seu código de acesso",
                "Seu código é: " + code);

        userProducer.publishMessageEmail(emailDto);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/auth/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody com.exemplo.secrest.dto.VerifyCodeDto dto) {
        boolean isValid = codigoCacheService.verifyCode(dto.email(), dto.code());
        
        if (isValid) {
            return ResponseEntity.ok("Token-Validado-Com-Sucesso");
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Código inválido ou expirado.");
    }



    @PostMapping("/update-profile")
    public ResponseEntity<UserProfileDto> updateProfile(Authentication authentication, @RequestBody UpdateProfileDto dto) {
        String email = authentication.getName();
        
        User updated = userService.updateProfile(email, dto);
        
        UserProfileDto responseDto = new UserProfileDto(
                updated.getName(), 
                updated.getEmail(), 
                updated.getRoles().get(0).getName()
        );
        
        return ResponseEntity.ok(responseDto);
    }
}

