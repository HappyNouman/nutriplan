package com.nutriplan.backend.plan;

import com.nutriplan.backend.meal.Meal;
import com.nutriplan.backend.meal.MealType;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Builds a 7-day plan (breakfast, lunch, dinner, snack each day) that matches a daily calorie target.
 *
 * For every day and meal type:
 * 1. The meal's calorie budget is its share of what is left of the day
 *    (breakfast 25%, lunch 35%, dinner 30%, snack 10%). Because it is based on what is LEFT,
 *    if breakfast came out a bit big, lunch gets a bit smaller, so the day still adds up.
 * 2. Candidates are meals of that type that were NOT eaten yesterday at the same meal
 *    and are used fewer than 2 times this week (for variety).
 * 3. Each candidate gets a portion (0.5 to 2.5 servings, in steps of 0.5) that brings it closest to the budget.
 * 4. One of the 3 candidates that end up closest to the budget is picked at random,
 *    so pressing "Generate" again gives a different but still accurate plan.
 */
@Component
public class PlanGenerator {

    static final Map<MealType, Double> CALORIE_SHARE = Map.of(
            MealType.BREAKFAST, 0.25,
            MealType.LUNCH, 0.35,
            MealType.DINNER, 0.30,
            MealType.SNACK, 0.10);
    static final int MAX_USES_PER_WEEK = 2;
    static final int PICK_FROM_BEST = 3;
    static final double MIN_SERVINGS = 0.5;
    static final double MAX_SERVINGS = 2.5;

    private final Random random;

    public PlanGenerator() {
        this(new Random());
    }

    /** Tests pass a Random with a fixed seed so results are repeatable. */
    PlanGenerator(Random random) {
        this.random = random;
    }

    public record PlannedMeal(DayOfWeek day, MealType mealType, Meal meal, double servings) {
        public double calories() {
            return meal.getCalories() * servings;
        }
    }

    public List<PlannedMeal> generate(List<Meal> meals, int dailyCalories) {
        Map<MealType, List<Meal>> mealsByType = meals.stream()
                .collect(Collectors.groupingBy(Meal::getMealType));
        for (MealType type : MealType.values()) {
            if (mealsByType.getOrDefault(type, List.of()).isEmpty()) {
                throw new IllegalStateException("No meals available for " + type);
            }
        }

        List<PlannedMeal> plan = new ArrayList<>();
        Map<Meal, Integer> timesUsed = new HashMap<>();
        Map<MealType, Meal> yesterday = new EnumMap<>(MealType.class);

        for (DayOfWeek day : DayOfWeek.values()) {
            // Track what is left of the day, so later meals make up for rounding in earlier ones
            double caloriesLeft = dailyCalories;
            double shareLeft = 1.0;

            for (MealType type : MealType.values()) {
                double share = CALORIE_SHARE.get(type);
                double budget = caloriesLeft * share / shareLeft;
                List<Meal> options = mealsByType.get(type);

                List<Meal> candidates = options.stream()
                        .filter(m -> m != yesterday.get(type))
                        .filter(m -> timesUsed.getOrDefault(m, 0) < MAX_USES_PER_WEEK)
                        .toList();
                if (candidates.isEmpty()) {
                    candidates = options; // not enough meals for full variety: allow repeats
                }

                List<Meal> best = candidates.stream()
                        .sorted(Comparator.comparingDouble(m -> Math.abs(servingsFor(m, budget) * m.getCalories() - budget)))
                        .limit(PICK_FROM_BEST)
                        .toList();
                Meal chosen = best.get(random.nextInt(best.size()));

                PlannedMeal planned = new PlannedMeal(day, type, chosen, servingsFor(chosen, budget));
                plan.add(planned);
                timesUsed.merge(chosen, 1, Integer::sum);
                yesterday.put(type, chosen);
                caloriesLeft -= planned.calories();
                shareLeft -= share;
            }
        }
        return plan;
    }

    /** Portion that brings the meal closest to the budget, rounded to 0.5 and kept between 0.5 and 2.5. */
    static double servingsFor(Meal meal, double budget) {
        double exact = budget / meal.getCalories();
        double rounded = Math.round(exact * 2) / 2.0;
        return Math.max(MIN_SERVINGS, Math.min(MAX_SERVINGS, rounded));
    }
}
