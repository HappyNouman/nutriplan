package com.nutriplan.backend.shopping;

import com.nutriplan.backend.shopping.dto.CheckItemRequest;
import com.nutriplan.backend.shopping.dto.ShoppingListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping-list")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    /** This week's shopping list, or 204 No Content if no plan was generated yet. */
    @GetMapping
    public ResponseEntity<ShoppingListResponse> current(@AuthenticationPrincipal Jwt jwt) {
        return shoppingListService.current(jwt.getSubject())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /** Tick / untick an item. */
    @PutMapping("/checked")
    public ResponseEntity<Void> setChecked(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody CheckItemRequest request) {
        shoppingListService.setChecked(jwt.getSubject(), request.key(), request.checked());
        return ResponseEntity.noContent().build();
    }
}
