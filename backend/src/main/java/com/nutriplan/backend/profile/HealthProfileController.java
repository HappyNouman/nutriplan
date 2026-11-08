package com.nutriplan.backend.profile;

import com.nutriplan.backend.profile.dto.HealthProfileRequest;
import com.nutriplan.backend.profile.dto.HealthProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/** The logged-in user's health profile. Who "me" is comes from the JWT, never from the request body. */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class HealthProfileController {

    private final HealthProfileService profileService;

    @GetMapping
    public HealthProfileResponse get(@AuthenticationPrincipal Jwt jwt) {
        return profileService.get(jwt.getSubject());
    }

    @PutMapping
    public HealthProfileResponse update(@AuthenticationPrincipal Jwt jwt,
                                        @Valid @RequestBody HealthProfileRequest request) {
        return profileService.update(jwt.getSubject(), request);
    }
}
