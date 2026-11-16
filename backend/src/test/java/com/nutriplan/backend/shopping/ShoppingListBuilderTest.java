package com.nutriplan.backend.shopping;

import com.nutriplan.backend.meal.*;
import com.nutriplan.backend.plan.PlanItem;
import com.nutriplan.backend.shopping.ShoppingListBuilder.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import static com.nutriplan.backend.meal.IngredientCategory.*;
import static com.nutriplan.backend.meal.IngredientUnit.*;
import static org.junit.jupiter.api.Assertions.*;

class ShoppingListBuilderTest {

    private static Meal meal(String name, MealIngredient... ingredients) {
        Meal meal = Meal.builder().name(name).mealType(MealType.LUNCH).calories(400).build();
        for (MealIngredient i : ingredients) {
            meal.addIngredient(i);
        }
        return meal;
    }

    private static PlanItem eat(Meal meal, double servings) {
        return new PlanItem(meal, DayOfWeek.MONDAY, meal.getMealType(), servings);
    }

    private static Item find(List<Item> list, String name) {
        return list.stream().filter(i -> i.name().equals(name)).findFirst()
                .orElseThrow(() -> new AssertionError(name + " is not on the list"));
    }

    @Test
    void sameIngredientFromDifferentMealsBecomesOneLine() {
        Meal omelette = meal("Omelette", new MealIngredient("Eggs", 2, PIECE, PROTEIN));
        Meal boiledEggs = meal("Boiled eggs", new MealIngredient("Eggs", 2, PIECE, PROTEIN));

        var list = ShoppingListBuilder.build(List.of(eat(omelette, 1), eat(boiledEggs, 1), eat(omelette, 1)), Set.of());

        assertEquals(1, list.size());
        assertEquals(6, find(list, "Eggs").quantity());
    }

    @Test
    void amountsAreMultipliedByServings() {
        Meal bowl = meal("Chicken bowl", new MealIngredient("Chicken breast", 150, GRAM, PROTEIN));

        var list = ShoppingListBuilder.build(List.of(eat(bowl, 1.5), eat(bowl, 0.5)), Set.of());

        assertEquals(300, find(list, "Chicken breast").quantity());  // 225 + 75
    }

    @ParameterizedTest(name = "{0} {1} -> {2}")
    @CsvSource({
            "2.5, PIECE, 3",     // can't buy half an egg
            "3.0, PIECE, 3",
            "0.5, PIECE, 1",
            "743, GRAM, 750",    // next 10 g
            "750, GRAM, 750",
            "1, ML, 10"
    })
    void roundsUpSoYouNeverBuyTooLittle(double quantity, IngredientUnit unit, int expected) {
        assertEquals(expected, ShoppingListBuilder.roundUp(quantity, unit));
    }

    @Test
    void sortedBySupermarketSectionThenByName() {
        Meal m = meal("Mix",
                new MealIngredient("Olive oil", 10, ML, PANTRY),
                new MealIngredient("Tomatoes", 80, GRAM, PRODUCE),
                new MealIngredient("Milk", 200, ML, DAIRY),
                new MealIngredient("Cucumbers", 80, GRAM, PRODUCE));

        var names = ShoppingListBuilder.build(List.of(eat(m, 1)), Set.of()).stream().map(Item::name).toList();

        assertEquals(List.of("Cucumbers", "Tomatoes", "Milk", "Olive oil"), names);
    }

    @Test
    void markedAsCheckedWhenTheKeyWasTicked() {
        Meal m = meal("Mix",
                new MealIngredient("Eggs", 2, PIECE, PROTEIN),
                new MealIngredient("Milk", 200, ML, DAIRY));

        var list = ShoppingListBuilder.build(List.of(eat(m, 1)), Set.of("eggs:piece"));

        assertTrue(find(list, "Eggs").checked());
        assertFalse(find(list, "Milk").checked());
    }

    @Test
    void sameNameWithDifferentUnitsStaysSeparate() {
        Meal m = meal("Mix",
                new MealIngredient("Garlic", 2, PIECE, PRODUCE),
                new MealIngredient("Garlic", 10, GRAM, PRODUCE));

        assertEquals(2, ShoppingListBuilder.build(List.of(eat(m, 1)), Set.of()).size());
    }

    @Test
    void emptyPlanGivesEmptyList() {
        assertTrue(ShoppingListBuilder.build(List.of(), Set.of()).isEmpty());
    }
}
