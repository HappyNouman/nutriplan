package com.nutriplan.backend.profile;

import com.nutriplan.backend.profile.dto.HealthProfileRequest;
import com.nutriplan.backend.profile.dto.HealthProfileResponse;
import com.nutriplan.backend.user.User;
import com.nutriplan.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class HealthProfileService {

    private final UserRepository userRepository;
    private final CalorieCalculator calculator;

    @Transactional(readOnly = true)
    public HealthProfileResponse get(String email) {
        User user = findUser(email);
        if (!isComplete(user)) {
            return HealthProfileResponse.incomplete(user);
        }
        return HealthProfileResponse.of(user, calculate(user));
    }

    @Transactional
    public HealthProfileResponse update(String email, HealthProfileRequest request) {
        User user = findUser(email);
        user.setGender(request.gender());
        user.setAge(request.age());
        user.setHeightCm(request.heightCm());
        user.setWeightKg(request.weightKg());
        user.setActivityLevel(request.activityLevel());
        user.setGoal(request.goal());

        CalorieCalculator.Result result = calculate(user);
        // Stored so the meal planner can use the target without recalculating
        user.setDailyCalories(result.dailyCalories());
        // No save() needed: the user is a managed entity, so Hibernate writes the changes when the transaction ends

        return HealthProfileResponse.of(user, result);
    }

    private CalorieCalculator.Result calculate(User u) {
        return calculator.calculate(u.getGender(), u.getAge(), u.getHeightCm(), u.getWeightKg(),
                u.getActivityLevel(), u.getGoal());
    }

    private static boolean isComplete(User u) {
        return u.getGender() != null && u.getAge() != null && u.getHeightCm() != null
                && u.getWeightKg() != null && u.getActivityLevel() != null && u.getGoal() != null;
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
