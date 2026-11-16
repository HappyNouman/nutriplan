package com.nutriplan.backend.shopping.dto;

import jakarta.validation.constraints.NotBlank;

/** Tick or untick one line of the shopping list. */
public record CheckItemRequest(@NotBlank String key, boolean checked) {
}
