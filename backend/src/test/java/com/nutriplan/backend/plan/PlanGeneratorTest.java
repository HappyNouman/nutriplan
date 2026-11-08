package com.nutriplan.backend.plan;

import com.nutriplan.backend.meal.Meal;
import com.nutriplan.backend.meal.MealType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PlanGeneratorTest {

    private final PlanGenerator generator = new PlanGenerator(new Random(42));

    /** A small, realistic meal list: 5 meals per type with different calorie levels. */
    private static List<Meal> sampleMeals() {
        List<Meal> meals = new ArrayList<>();
        int[][] caloriesByType = {
                {255, 320, 345, 385, 420},   // breakfast
                {380, 450, 480, 520, 560},   // lunch
                {370, 405, 425, 465, 510},   // dinner
                {140, 150, 185, 215, 260}};  // snack
        MealType[] types = MealType.values();
        for (int t = 0; t < types.length; t++) {
            for (int calories : caloriesByType[t]) {
                meals.add(Meal.builder().name(types[t] + " " + calories).mealType(types[t])
                        .calories(calories).build());
            }
        }
        return meals;
    }

    @Test
    void createsFourMealsForEachOfSevenDays() {
        var plan = generator.generate(sampleMeals(), 2000);

        assertEquals(28, plan.size());
        for (DayOfWeek day : DayOfWeek.values()) {
            var types = plan.stream().filter(p -> p.day() == day).map(PlanGenerator.PlannedMeal::mealType).toList();
            assertEquals(List.of(MealType.values()), types, "each day has breakfast, lunch, dinner, snack in order");
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1200, 1546, 2000, 2720, 3200})
    void everyDayIsWithinTenPercentOfTarget(int target) {
        // The generator is random, so check many different random plans, not just one
        for (int seed = 0; seed < 20; seed++) {
            var plan = new PlanGenerator(new Random(seed)).generate(sampleMeals(), target);

            Map<DayOfWeek, Double> dayTotals = plan.stream().collect(
                    Collectors.groupingBy(PlanGenerator.PlannedMeal::day,
                            Collectors.summingDouble(PlanGenerator.PlannedMeal::calories)));
            int s = seed;
            dayTotals.forEach((day, total) ->
                    assertEquals(target, total, target * 0.10, "seed " + s + ", " + day + " total was " + total));
        }
    }

    @Test
    void sameMealIsNotRepeatedOnConsecutiveDays() {
        var plan = generator.generate(sampleMeals(), 2000);

        for (MealType type : MealType.values()) {
            var sequence = plan.stream().filter(p -> p.mealType() == type).map(PlanGenerator.PlannedMeal::meal).toList();
            for (int i = 1; i < sequence.size(); i++) {
                assertNotSame(sequence.get(i - 1), sequence.get(i), type + " repeated on day " + (i + 1));
            }
        }
    }

    @Test
    void noMealIsUsedMoreThanTwiceAWeek() {
        var plan = generator.generate(sampleMeals(), 2000);

        Map<Meal, Long> uses = plan.stream().collect(
                Collectors.groupingBy(PlanGenerator.PlannedMeal::meal, Collectors.counting()));
        uses.forEach((meal, count) -> assertTrue(count <= 2, meal.getName() + " used " + count + " times"));
    }

    @Test
    void servingsAreRoundedToHalvesAndKeptInRange() {
        Meal meal = Meal.builder().calories(400).build();

        assertEquals(1.0, PlanGenerator.servingsFor(meal, 410));   // 1.03 -> 1.0
        assertEquals(1.5, PlanGenerator.servingsFor(meal, 580));   // 1.45 -> 1.5
        assertEquals(0.5, PlanGenerator.servingsFor(meal, 50));    // too small -> minimum 0.5
        assertEquals(2.5, PlanGenerator.servingsFor(meal, 2000));  // too big -> maximum 2.5
    }

    @Test
    void failsClearlyWhenAMealTypeHasNoMeals() {
        var onlyBreakfasts = sampleMeals().stream().filter(m -> m.getMealType() == MealType.BREAKFAST).toList();

        var error = assertThrows(IllegalStateException.class, () -> generator.generate(onlyBreakfasts, 2000));
        assertTrue(error.getMessage().contains("LUNCH"));
    }
}
