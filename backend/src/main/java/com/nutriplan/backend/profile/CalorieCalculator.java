package com.nutriplan.backend.profile;

import com.nutriplan.backend.user.ActivityLevel;
import com.nutriplan.backend.user.Gender;
import com.nutriplan.backend.user.Goal;
import org.springframework.stereotype.Component;

/**
 * Calculates daily energy needs from a user's body data.
 *
 * 1. BMR  (Basal Metabolic Rate): calories burned at complete rest. Mifflin-St Jeor formula:
 *      men:   10 * weight(kg) + 6.25 * height(cm) - 5 * age + 5
 *      women: 10 * weight(kg) + 6.25 * height(cm) - 5 * age - 161
 * 2. TDEE (Total Daily Energy Expenditure) = BMR * activity factor.
 * 3. Daily target = TDEE adjusted for the goal (-500 to lose, +300 to gain),
 *    never below a safe minimum (1200 kcal women / 1500 kcal men).
 * 4. Macros: the daily target split into protein / carbs / fat by percentage
 *    (protein and carbs have 4 kcal per gram, fat has 9).
 */
@Component
public class CalorieCalculator {

    static final int LOSE_WEIGHT_DEFICIT = 500;
    static final int GAIN_MUSCLE_SURPLUS = 300;
    static final int MIN_CALORIES_FEMALE = 1200;
    static final int MIN_CALORIES_MALE = 1500;

    public record Result(int bmr, int tdee, int dailyCalories,
                         int proteinG, int carbsG, int fatG, double bmi) {
    }

    /** Percent of daily calories from each macro. */
    private record MacroSplit(double protein, double carbs, double fat) {
    }

    public Result calculate(Gender gender, int age, double heightCm, double weightKg,
                            ActivityLevel activityLevel, Goal goal) {
        double bmr = 10 * weightKg + 6.25 * heightCm - 5 * age + (gender == Gender.MALE ? 5 : -161);
        double tdee = bmr * activityFactor(activityLevel);

        int minimum = gender == Gender.MALE ? MIN_CALORIES_MALE : MIN_CALORIES_FEMALE;
        int daily = (int) Math.max(minimum, Math.round(tdee + goalAdjustment(goal)));

        MacroSplit split = macroSplit(goal);
        int proteinG = (int) Math.round(daily * split.protein() / 4);
        int carbsG = (int) Math.round(daily * split.carbs() / 4);
        int fatG = (int) Math.round(daily * split.fat() / 9);

        double heightM = heightCm / 100;
        double bmi = Math.round(weightKg / (heightM * heightM) * 10) / 10.0;

        return new Result((int) Math.round(bmr), (int) Math.round(tdee), daily, proteinG, carbsG, fatG, bmi);
    }

    static double activityFactor(ActivityLevel level) {
        return switch (level) {
            case SEDENTARY -> 1.2;     // little or no exercise
            case LIGHT -> 1.375;       // exercise 1-3 days/week
            case MODERATE -> 1.55;     // exercise 3-5 days/week
            case ACTIVE -> 1.725;      // exercise 6-7 days/week
            case VERY_ACTIVE -> 1.9;   // hard exercise + physical job
        };
    }

    private static int goalAdjustment(Goal goal) {
        return switch (goal) {
            case LOSE_WEIGHT -> -LOSE_WEIGHT_DEFICIT;
            case MAINTAIN -> 0;
            case GAIN_MUSCLE -> GAIN_MUSCLE_SURPLUS;
        };
    }

    private static MacroSplit macroSplit(Goal goal) {
        return switch (goal) {
            case LOSE_WEIGHT -> new MacroSplit(0.30, 0.40, 0.30);  // more protein keeps you full and protects muscle
            case MAINTAIN -> new MacroSplit(0.25, 0.50, 0.25);
            case GAIN_MUSCLE -> new MacroSplit(0.30, 0.45, 0.25);
        };
    }
}
