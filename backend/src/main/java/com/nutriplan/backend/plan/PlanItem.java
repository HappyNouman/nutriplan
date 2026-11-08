package com.nutriplan.backend.plan;

import com.nutriplan.backend.meal.Meal;
import com.nutriplan.backend.meal.MealType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;

/** One meal in a plan: e.g. "Tuesday lunch = Grilled chicken quinoa bowl, 1.5 servings". */
@Entity
@Table(name = "plan_items")
@Getter @Setter
@NoArgsConstructor
public class PlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id")
    private MealPlan plan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    /** Portion size, in steps of 0.5, so the day adds up to the calorie target. */
    private double servings;

    public PlanItem(Meal meal, DayOfWeek dayOfWeek, MealType mealType, double servings) {
        this.meal = meal;
        this.dayOfWeek = dayOfWeek;
        this.mealType = mealType;
        this.servings = servings;
    }
}
