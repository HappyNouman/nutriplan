package com.nutriplan.backend.meal;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/** One healthy meal (one serving) with its nutrition values. */
@Entity
@Table(name = "meals")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

    @Column(length = 500)
    private String description;

    private int calories;
    private int proteinG;
    private int carbsG;
    private int fatG;

    /** What one serving is made of. Used to build the shopping list. */
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MealIngredient> ingredients = new ArrayList<>();

    public void addIngredient(MealIngredient ingredient) {
        ingredient.setMeal(this);
        ingredients.add(ingredient);
    }
}
