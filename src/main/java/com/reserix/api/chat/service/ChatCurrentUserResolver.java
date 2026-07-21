package com.reserix.api.chat.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ChatCurrentUserResolver {

    /*
     * This is intentionally conservative.
     *
     * If your Security principal already contains numeric user id, this works.
     * If authentication.getName() is email/username, this returns null.
     *
     * Later, replace this with your real JWT/User principal resolver.
     */
    public Long resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String name = authentication.getName();
        if (name == null || name.isBlank()) {
            return null;
        }

        try {
            return Long.valueOf(name);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
