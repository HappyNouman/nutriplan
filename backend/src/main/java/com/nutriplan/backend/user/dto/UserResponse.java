package com.nutriplan.backend.user.dto;

import com.nutriplan.backend.user.ActivityLevel;
import com.nutriplan.backend.user.Gender;
import com.nutriplan.backend.user.Goal;
import com.nutriplan.backend.user.User;

/** What the API returns about a user. Never includes the password hash. */
public record UserResponse(
        Long id,
        String fullName,
        String email,
        Gender gender,
        Integer age,
        Double heightCm,
        Double weightKg,
        ActivityLevel activityLevel,
        Goal goal,
        Integer dailyCalories
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getFullName(), u.getEmail(), u.getGender(), u.getAge(),
                u.getHeightCm(), u.getWeightKg(), u.getActivityLevel(), u.getGoal(), u.getDailyCalories());
    }
}
