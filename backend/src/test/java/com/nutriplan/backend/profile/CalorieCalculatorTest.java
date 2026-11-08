package com.nutriplan.backend.profile;

import com.nutriplan.backend.user.ActivityLevel;
import com.nutriplan.backend.user.Gender;
import com.nutriplan.backend.user.Goal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Plain unit tests: no Spring, no database, so they run in milliseconds. */
class CalorieCalculatorTest {

    private final CalorieCalculator calculator = new CalorieCalculator();

    @Test
    void maleModerateMaintain() {
        // BMR = 10*75 + 6.25*180 - 5*25 + 5 = 1755 ; TDEE = 1755 * 1.55 = 2720.25
        var r = calculator.calculate(Gender.MALE, 25, 180, 75, ActivityLevel.MODERATE, Goal.MAINTAIN);

        assertEquals(1755, r.bmr());
        assertEquals(2720, r.tdee());
        assertEquals(2720, r.dailyCalories());
        // 25% / 50% / 25% of 2720
        assertEquals(170, r.proteinG());  // 680 kcal / 4
        assertEquals(340, r.carbsG());    // 1360 kcal / 4
        assertEquals(76, r.fatG());       // 680 kcal / 9 = 75.6
        assertEquals(23.1, r.bmi());      // 75 / 1.8^2
    }

    @Test
    void femaleFormulaUsesMinus161() {
        // BMR = 10*60 + 6.25*165 - 5*30 - 161 = 1320.25
        var r = calculator.calculate(Gender.FEMALE, 30, 165, 60, ActivityLevel.MODERATE, Goal.MAINTAIN);

        assertEquals(1320, r.bmr());
        assertEquals(2046, r.tdee());  // 1320.25 * 1.55 = 2046.39
    }

    @Test
    void gainMuscleAdds300() {
        // TDEE = 1755 * 1.375 = 2413.125 ; + 300 = 2713
        var r = calculator.calculate(Gender.MALE, 25, 180, 75, ActivityLevel.LIGHT, Goal.GAIN_MUSCLE);

        assertEquals(2713, r.dailyCalories());
    }

    @Test
    void loseWeightSubtracts500() {
        // TDEE = 2720.25 ; - 500 = 2220
        var r = calculator.calculate(Gender.MALE, 25, 180, 75, ActivityLevel.MODERATE, Goal.LOSE_WEIGHT);

        assertEquals(2220, r.dailyCalories());
    }

    @Test
    void neverGoesBelowSafeMinimum() {
        // TDEE = 1320.25 * 1.2 = 1584 ; - 500 = 1084 -> raised to the 1200 minimum for women
        var r = calculator.calculate(Gender.FEMALE, 30, 165, 60, ActivityLevel.SEDENTARY, Goal.LOSE_WEIGHT);

        assertEquals(1200, r.dailyCalories());
    }

    @ParameterizedTest
    @CsvSource({"SEDENTARY, 1.2", "LIGHT, 1.375", "MODERATE, 1.55", "ACTIVE, 1.725", "VERY_ACTIVE, 1.9"})
    void activityFactors(ActivityLevel level, double expected) {
        assertEquals(expected, CalorieCalculator.activityFactor(level));
    }
}
