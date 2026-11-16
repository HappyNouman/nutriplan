package com.nutriplan.backend.shopping.dto;

import com.nutriplan.backend.meal.IngredientCategory;
import com.nutriplan.backend.shopping.ShoppingListBuilder.Item;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** The shopping list grouped by supermarket section, e.g. { category: PRODUCE, items: [...] }. */
public record ShoppingListResponse(LocalDate weekStart, int totalItems, int checkedItems,
                                   List<Section> sections) {

    public record Section(IngredientCategory category, List<Item> items) {
    }

    public static ShoppingListResponse of(LocalDate weekStart, List<Item> items) {
        // One section per category that has at least one item
        List<Section> sections = new ArrayList<>();
        for (IngredientCategory category : IngredientCategory.values()) {
            List<Item> itemsInCategory = new ArrayList<>();
            for (Item item : items) {
                if (item.category() == category) {
                    itemsInCategory.add(item);
                }
            }
            if (!itemsInCategory.isEmpty()) {
                sections.add(new Section(category, itemsInCategory));
            }
        }

        int checked = 0;
        for (Item item : items) {
            if (item.checked()) {
                checked++;
            }
        }
        return new ShoppingListResponse(weekStart, items.size(), checked, sections);
    }
}
