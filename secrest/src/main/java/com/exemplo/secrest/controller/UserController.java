package com.exemplo.secrest.controller;

import com.exemplo.secrest.dto.CreateUserDto;
import com.exemplo.secrest.dto.EmailDto;
import com.exemplo.secrest.dto.LoginUserDto;
import com.exemplo.secrest.dto.RecoveryJwtTokenDto;
import com.exemplo.secrest.dto.RequestCodeDto;
import com.exemplo.secrest.producer.UserProducer;
import com.exemplo.secrest.repository.UserRepository;
import com.exemplo.secrest.service.CodigoCacheService;
import com.exemplo.secrest.service.UserService;
import com.exemplo.secrest.entity.User;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

            newUser.setRole(Role.ROLE_CUSTOMER);
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
}