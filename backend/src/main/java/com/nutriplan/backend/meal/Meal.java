package com.nutriplan.backend.meal;

import jakarta.persistence.*;
import lombok.*;

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
}
