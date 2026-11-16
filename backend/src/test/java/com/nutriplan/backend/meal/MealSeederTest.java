package com.nutriplan.backend.meal;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Checks the starter data, so a typo there can't break the shopping list. */
class MealSeederTest {

    private final List<Meal> meals = MealSeeder.seedMeals();

    @Test
    void everyMealHasIngredients() {
        for (Meal meal : meals) {
            assertFalse(meal.getIngredients().isEmpty(), meal.getName() + " has no ingredients");
        }
    }

    @Test
    void amountsArePositive() {
        for (Meal meal : meals) {
            for (MealIngredient ingredient : meal.getIngredients()) {
                assertTrue(ingredient.getQuantity() > 0, ingredient.getName() + " in " + meal.getName());
            }
        }
    }

    @Test
    void anIngredientAlwaysUsesTheSameUnitAndSection() {
        // "Eggs" must be PIECE + PROTEIN everywhere, or the shopping list would show it twice
        Map<String, MealIngredient> firstUse = new HashMap<>();
        for (Meal meal : meals) {
            for (MealIngredient ingredient : meal.getIngredients()) {
                MealIngredient first = firstUse.get(ingredient.getName());
                if (first == null) {
                    firstUse.put(ingredient.getName(), ingredient);
                } else {
                    assertEquals(first.getUnit(), ingredient.getUnit(), ingredient.getName() + " in " + meal.getName());
                    assertEquals(first.getCategory(), ingredient.getCategory(), ingredient.getName() + " in " + meal.getName());
                }
            }
        }
    }
}
