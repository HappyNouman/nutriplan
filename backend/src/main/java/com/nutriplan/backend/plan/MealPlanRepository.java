package com.nutriplan.backend.plan;

import com.nutriplan.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {
    Optional<MealPlan> findByUserAndWeekStart(User user, LocalDate weekStart);
}
