package com.nutriplan.backend.meal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** How much of one ingredient ONE serving of a meal needs, e.g. "Chicken breast, 150 g". */
@Entity
@Table(name = "meal_ingredients")
@Getter @Setter
@NoArgsConstructor
public class MealIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @Column(nullable = false)
    private String name;

    /** Amount for one serving. */
    private double quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IngredientUnit unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IngredientCategory category;

    public MealIngredient(String name, double quantity, IngredientUnit unit, IngredientCategory category) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
    }
}
