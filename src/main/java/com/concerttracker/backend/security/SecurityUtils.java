package com.concerttracker.backend.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public Long getCurrentUserId() {
        return getCurrentUserDetails().getUserId();
    }

    public String getCurrentUserEmail() {
        return getCurrentUserDetails().getUsername();
    }

    private CustomUserDetails getCurrentUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new IllegalStateException("No hay un usuario autenticado en el contexto actual.");
        }
        return userDetails;
    }
}