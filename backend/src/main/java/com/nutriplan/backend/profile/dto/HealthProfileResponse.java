package com.nutriplan.backend.profile.dto;

import com.nutriplan.backend.profile.CalorieCalculator;
import com.nutriplan.backend.user.ActivityLevel;
import com.nutriplan.backend.user.Gender;
import com.nutriplan.backend.user.Goal;
import com.nutriplan.backend.user.User;

/**
 * The saved profile plus the calculated numbers.
 * "complete" is false (and the numbers are null) until the user fills the profile for the first time.
 */
public record HealthProfileResponse(
        Gender gender,
        Integer age,
        Double heightCm,
        Double weightKg,
        ActivityLevel activityLevel,
        Goal goal,
        boolean complete,
        Integer bmr,
        Integer tdee,
        Integer dailyCalories,
        Integer proteinG,
        Integer carbsG,
        Integer fatG,
        Double bmi
) {
    public static HealthProfileResponse incomplete(User u) {
        return new HealthProfileResponse(u.getGender(), u.getAge(), u.getHeightCm(), u.getWeightKg(),
                u.getActivityLevel(), u.getGoal(), false, null, null, null, null, null, null, null);
    }

    public static HealthProfileResponse of(User u, CalorieCalculator.Result r) {
        return new HealthProfileResponse(u.getGender(), u.getAge(), u.getHeightCm(), u.getWeightKg(),
                u.getActivityLevel(), u.getGoal(), true, r.bmr(), r.tdee(), r.dailyCalories(),
                r.proteinG(), r.carbsG(), r.fatG(), r.bmi());
    }
}
