package com.nutriplan.backend.plan.dto;

import com.nutriplan.backend.meal.MealType;
import com.nutriplan.backend.plan.MealPlan;
import com.nutriplan.backend.plan.PlanItem;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A week plan grouped by day, ready for the frontend:
 * days[0] = Monday { totals, meals: [breakfast, lunch, dinner, snack] }, ...
 */
public record PlanResponse(Long id, LocalDate weekStart, int targetCalories,
                           LocalDateTime generatedAt, List<Day> days) {

    public record Day(DayOfWeek day, LocalDate date, int calories,
                      int proteinG, int carbsG, int fatG, List<PlannedMeal> meals) {
    }

    /** Nutrition values are already multiplied by the number of servings. */
    public record PlannedMeal(MealType mealType, Long mealId, String name, String description,
                              double servings, int calories, int proteinG, int carbsG, int fatG) {
    }

    public static PlanResponse from(MealPlan plan) {
        Map<DayOfWeek, List<PlanItem>> itemsByDay = plan.getItems().stream()
                .collect(Collectors.groupingBy(PlanItem::getDayOfWeek));

        List<Day> days = itemsByDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())  // enum order: MONDAY ... SUNDAY
                .map(e -> toDay(plan.getWeekStart(), e.getKey(), e.getValue()))
                .toList();

        return new PlanResponse(plan.getId(), plan.getWeekStart(), plan.getTargetCalories(),
                plan.getGeneratedAt(), days);
    }

    private static Day toDay(LocalDate weekStart, DayOfWeek day, List<PlanItem> items) {
        List<PlannedMeal> meals = items.stream()
                .sorted(Comparator.comparing(PlanItem::getMealType))  // BREAKFAST, LUNCH, DINNER, SNACK
                .map(PlanResponse::toMeal)
                .toList();
        return new Day(day, weekStart.plusDays(day.getValue() - 1),
                meals.stream().mapToInt(PlannedMeal::calories).sum(),
                meals.stream().mapToInt(PlannedMeal::proteinG).sum(),
                meals.stream().mapToInt(PlannedMeal::carbsG).sum(),
                meals.stream().mapToInt(PlannedMeal::fatG).sum(),
                meals);
    }

    private static PlannedMeal toMeal(PlanItem item) {
        var m = item.getMeal();
        double s = item.getServings();
        return new PlannedMeal(item.getMealType(), m.getId(), m.getName(), m.getDescription(), s,
                (int) Math.round(m.getCalories() * s), (int) Math.round(m.getProteinG() * s),
                (int) Math.round(m.getCarbsG() * s), (int) Math.round(m.getFatG() * s));
    }
}
