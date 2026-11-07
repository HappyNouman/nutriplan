package com.nutriplan.backend.auth.dto;

import com.nutriplan.backend.user.dto.UserResponse;

public record AuthResponse(String token, UserResponse user) {
}
