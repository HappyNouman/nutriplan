package com.nutriplan.backend.profile.dto;

import com.nutriplan.backend.user.ActivityLevel;
import com.nutriplan.backend.user.Gender;
import com.nutriplan.backend.user.Goal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Body data sent from the profile form. Ranges reject typos like a height of 17 cm or a weight of 700 kg. */
public record HealthProfileRequest(
        @NotNull Gender gender,
        @NotNull @Min(15) @Max(100) Integer age,
        @NotNull @DecimalMin("100") @DecimalMax("250") Double heightCm,
        @NotNull @DecimalMin("30") @DecimalMax("300") Double weightKg,
        @NotNull ActivityLevel activityLevel,
        @NotNull Goal goal
) {
}
