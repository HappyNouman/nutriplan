package com.nutriplan.backend.plan;

import com.nutriplan.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** A user's meal plan for one week (Monday to Sunday). One plan per user per week. */
@Entity
@Table(name = "meal_plans",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "week_start"}))
@Getter @Setter
@NoArgsConstructor
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /** The Monday of the planned week. */
    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    /** The user's daily calorie target when the plan was generated. */
    private int targetCalories;

    @Column(nullable = false)
    private LocalDateTime generatedAt;

    // cascade: saving the plan saves its items; orphanRemoval: items removed from the list are deleted
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanItem> items = new ArrayList<>();

    public MealPlan(User user, LocalDate weekStart) {
        this.user = user;
        this.weekStart = weekStart;
    }

    public void addItem(PlanItem item) {
        item.setPlan(this);
        items.add(item);
    }
}
