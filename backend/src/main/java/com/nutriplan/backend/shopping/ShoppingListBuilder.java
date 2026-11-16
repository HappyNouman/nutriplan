package com.nutriplan.backend.shopping;

import com.nutriplan.backend.meal.IngredientCategory;
import com.nutriplan.backend.meal.IngredientUnit;
import com.nutriplan.backend.meal.MealIngredient;
import com.nutriplan.backend.plan.PlanItem;

import java.util.*;

/**
 * Turns a week of planned meals into one shopping list.
 *
 * Each plan item = one meal + how many servings.
 * Each meal knows its ingredients for ONE serving.
 * So: amount needed = amount per serving × servings, added up for the whole week.
 */
public final class ShoppingListBuilder {

    private ShoppingListBuilder() {
    }

    /** One line of the shopping list, e.g. Eggs, 14 pieces. */
    public record Item(String key, String name, IngredientCategory category,
                       IngredientUnit unit, int quantity, boolean checked) {
    }

    /** Id of a line, e.g. "eggs:piece". Eggs at breakfast and eggs as a snack get the same key. */
    public static String keyOf(String name, IngredientUnit unit) {
        return name.toLowerCase() + ":" + unit.name().toLowerCase();
    }

    public static List<Item> build(List<PlanItem> planItems, Set<String> checkedKeys) {
        // Step 1: add up how much of each ingredient the whole week needs
        Map<String, Double> totals = new HashMap<>();
        Map<String, MealIngredient> ingredientByKey = new HashMap<>();

        for (PlanItem planItem : planItems) {
            for (MealIngredient ingredient : planItem.getMeal().getIngredients()) {
                String key = keyOf(ingredient.getName(), ingredient.getUnit());
                double amount = ingredient.getQuantity() * planItem.getServings();

                totals.put(key, totals.getOrDefault(key, 0.0) + amount);
                ingredientByKey.put(key, ingredient);
            }
        }

        // Step 2: make one shopping line per ingredient
        List<Item> items = new ArrayList<>();
        for (String key : totals.keySet()) {
            MealIngredient ingredient = ingredientByKey.get(key);
            int quantity = roundUp(totals.get(key), ingredient.getUnit());
            boolean checked = checkedKeys.contains(key);
            items.add(new Item(key, ingredient.getName(), ingredient.getCategory(),
                    ingredient.getUnit(), quantity, checked));
        }

        // Step 3: sort by supermarket section (the enum's order), then by name
        items.sort(Comparator.comparing(Item::category).thenComparing(Item::name));
        return items;
    }

    /**
     * Always round UP so you never buy too little:
     * pieces to a whole number (2.5 eggs → 3), grams and ml to the next 10 (743 g → 750 g).
     */
    static int roundUp(double quantity, IngredientUnit unit) {
        if (unit == IngredientUnit.PIECE) {
            return (int) Math.ceil(quantity);
        }
        return (int) Math.ceil(quantity / 10) * 10;
    }
}
