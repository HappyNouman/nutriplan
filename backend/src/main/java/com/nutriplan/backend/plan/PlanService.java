package com.nutriplan.backend.plan;

import com.nutriplan.backend.meal.MealRepository;
import com.nutriplan.backend.plan.dto.PlanResponse;
import com.nutriplan.backend.user.User;
import com.nutriplan.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final MealPlanRepository planRepository;
    private final PlanGenerator generator;

    /** Creates this week's plan, or replaces it if one already exists ("Regenerate"). */
    @Transactional
    public PlanResponse generate(String email) {
        User user = findUser(email);
        if (user.getDailyCalories() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Please complete your health profile first, so we know your calorie target");
        }

        LocalDate weekStart = currentWeekStart();
        MealPlan plan = planRepository.findByUserAndWeekStart(user, weekStart)
                .orElseGet(() -> new MealPlan(user, weekStart));

        plan.getItems().clear();  // orphanRemoval deletes the old items
        plan.setTargetCalories(user.getDailyCalories());
        plan.setGeneratedAt(LocalDateTime.now());
        generator.generate(mealRepository.findAll(), user.getDailyCalories())
                .forEach(p -> plan.addItem(new PlanItem(p.meal(), p.day(), p.mealType(), p.servings())));

        planRepository.save(plan);  // cascade saves the items too
        return PlanResponse.from(plan);
    }

    @Transactional(readOnly = true)
    public Optional<PlanResponse> current(String email) {
        return planRepository.findByUserAndWeekStart(findUser(email), currentWeekStart())
                .map(PlanResponse::from);
    }

    private static LocalDate currentWeekStart() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
