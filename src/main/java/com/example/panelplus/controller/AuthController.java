package com.example.panelplus.controller;

import com.example.panelplus.dto.request.LoginRequest;
import com.example.panelplus.dto.request.RegisterRequest;
import com.example.panelplus.dto.response.TokenResponse;
import com.example.panelplus.entity.User;
import com.example.panelplus.exception.BaseException;
import com.example.panelplus.repository.UserRepository;
import com.example.panelplus.security.JwtService;
import com.example.panelplus.util.ApiResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new BaseException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtService.generateToken(request.username());
        return ok(new TokenResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenResponse>> register(@RequestBody RegisterRequest request) {
        // Basic validations
        if (request.username() == null || request.username().isBlank() ||
                request.password() == null || request.password().isBlank() ||
                request.email() == null || request.email().isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.<TokenResponse>error(HttpStatus.BAD_REQUEST, "username, email ve password zorunludur"));
        }

        // Uniqueness checks
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.<TokenResponse>error(HttpStatus.BAD_REQUEST, "Kullanıcı adı zaten mevcut"));
        }
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.<TokenResponse>error(HttpStatus.BAD_REQUEST, "Email zaten kayıtlı"));
        }

        // Create and persist user
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .createdAt(now)
                .updatedAt(now)
                .build();
        userRepository.save(user);

        // Auto-login: generate token
        String token = jwtService.generateToken(user.getUsername());
        return created(new TokenResponse(token));
    }
}
