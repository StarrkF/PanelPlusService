package com.example.panelplus.security;

import com.example.panelplus.entity.User;
import com.example.panelplus.exception.BaseException;
import com.example.panelplus.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Uygulama genelinde geçerli kullanıcıya (Authentication) erişmek ve
 * buna karşılık gelen User entity'sini getirmek için yardımcı sınıf.
 */
@Component
@RequiredArgsConstructor
public class AuthUtil {

    private static AuthUtil instance;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        instance = this;
    }

    /**
     * Geçerli authentication içindeki kullanıcı adını (username) döndürür.
     */
    public static Optional<String> getCurrentUsernameOptional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            return Optional.ofNullable(userDetails.getUsername());
        }
        if (principal instanceof String s) { // Bazı durumlarda principal bir String olabilir
            // anonymousUser kontrolü
            if ("anonymousUser".equalsIgnoreCase(s)) return Optional.empty();
            return Optional.of(s);
        }
        return Optional.empty();
    }

    /**
     * Geçerli kullanıcı adından User entity'sini Optional olarak getirir.
     */
    public static Optional<User> getCurrentUserOptional() {
        if (instance == null) return Optional.empty();
        return getCurrentUsernameOptional()
                .flatMap(username -> instance.userRepository.findByUsername(username));
    }

    /**
     * Geçerli kullanıcı yoksa 401 dönen bir BaseException fırlatır.
     */
    public static User getCurrentUserOrThrow() {
        return getCurrentUserOptional()
                .orElseThrow(() -> new BaseException("Unauthorized", HttpStatus.UNAUTHORIZED));
    }
}
