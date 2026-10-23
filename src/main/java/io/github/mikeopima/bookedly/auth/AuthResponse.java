package io.github.mikeopima.bookedly.auth;

import io.github.mikeopima.bookedly.user.UserResponse;

/** Returned after a successful sign-up or log-in. */
public record AuthResponse(String token, String tokenType, long expiresIn, UserResponse user) {
}
