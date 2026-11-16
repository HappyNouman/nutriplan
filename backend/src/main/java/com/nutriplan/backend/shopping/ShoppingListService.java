package com.nutriplan.backend.shopping;

import com.nutriplan.backend.plan.MealPlan;
import com.nutriplan.backend.plan.PlanService;
import com.nutriplan.backend.shopping.dto.ShoppingListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShoppingListService {

    private final PlanService planService;

    /** The list for this week's plan, or empty if there is no plan yet. */
    @Transactional(readOnly = true)
    public Optional<ShoppingListResponse> current(String email) {
        return planService.findCurrentPlan(email)
                .map(plan -> ShoppingListResponse.of(plan.getWeekStart(),
                        ShoppingListBuilder.build(plan.getItems(), plan.getCheckedItems())));
    }

    @Transactional
    public void setChecked(String email, String key, boolean checked) {
        MealPlan plan = planService.findCurrentPlan(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Generate a meal plan first"));

        // Only allow keys that are really on this week's list
        boolean onTheList = false;
        for (ShoppingListBuilder.Item item : ShoppingListBuilder.build(plan.getItems(), plan.getCheckedItems())) {
            if (item.key().equals(key)) {
                onTheList = true;
            }
        }
        if (!onTheList) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This item is not on this week's list");
        }

        if (checked) {
            plan.getCheckedItems().add(key);
        } else {
            plan.getCheckedItems().remove(key);
        }
        // No save() needed: the plan is managed, so Hibernate writes the change when the transaction ends
    }
}
