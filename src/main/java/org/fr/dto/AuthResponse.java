package org.fr.dto;

public record AuthResponse(String accessToken, String refreshToken, long accessTokenExpiresInMs) {
}
