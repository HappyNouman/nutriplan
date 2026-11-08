package com.nutriplan.backend.meal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.nutriplan.backend.meal.MealType.*;

/**
 * Fills the meals table with starter data the first time the app runs (when the table is empty).
 * Values are per serving; calories ≈ 4*protein + 4*carbs + 9*fat.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MealSeeder implements ApplicationRunner {

    private final MealRepository mealRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (mealRepository.count() > 0) {
            return;
        }
        List<Meal> meals = List.of(
                // Breakfast
                meal("Greek yogurt parfait", BREAKFAST, "Greek yogurt layered with mixed berries and a little granola.", 320, 20, 42, 8),
                meal("Veggie omelette with toast", BREAKFAST, "Two-egg omelette with spinach, tomato and peppers, with a slice of whole-wheat toast.", 345, 24, 26, 16),
                meal("Banana peanut butter oatmeal", BREAKFAST, "Oats cooked in milk, topped with banana slices and a spoon of peanut butter.", 410, 13, 62, 12),
                meal("Avocado toast with poached eggs", BREAKFAST, "Whole-grain toast with smashed avocado and two poached eggs.", 385, 17, 32, 21),
                meal("Foul medames with baladi bread", BREAKFAST, "Fava beans with olive oil, cumin and lemon, served with whole-wheat baladi bread.", 380, 19, 55, 9),
                meal("Cottage cheese fruit bowl", BREAKFAST, "Cottage cheese with pineapple, kiwi and a sprinkle of chia seeds.", 255, 24, 28, 5),
                meal("Protein pancakes with honey", BREAKFAST, "Oat and egg-white pancakes with a drizzle of honey and berries.", 420, 28, 54, 10),
                meal("Shakshuka with feta", BREAKFAST, "Eggs poached in a spiced tomato and pepper sauce, topped with feta.", 340, 19, 18, 21),

                // Lunch
                meal("Grilled chicken quinoa bowl", LUNCH, "Grilled chicken breast over quinoa with cucumber, tomato and lemon dressing.", 510, 42, 48, 16),
                meal("Turkey hummus wrap", LUNCH, "Whole-wheat wrap with turkey slices, hummus, lettuce and grated carrot.", 450, 32, 44, 16),
                meal("Lentil soup with salad", LUNCH, "Egyptian red lentil soup with a fresh green salad on the side.", 380, 20, 56, 8),
                meal("Tuna chickpea salad", LUNCH, "Tuna, chickpeas, red onion, parsley and olive oil lemon dressing.", 410, 34, 30, 17),
                meal("Beef and broccoli with brown rice", LUNCH, "Lean beef strips stir-fried with broccoli, served with brown rice.", 550, 38, 58, 18),
                meal("Grilled salmon with sweet potato", LUNCH, "Salmon fillet with roasted sweet potato wedges and green beans.", 560, 36, 44, 26),
                meal("Chicken shawarma salad bowl", LUNCH, "Spiced chicken shawarma over salad with garlic yogurt sauce.", 480, 38, 30, 23),
                meal("Light koshari", LUNCH, "A lighter portion of koshari with lentils, rice, pasta and spicy tomato sauce.", 520, 17, 92, 9),
                meal("Falafel bowl with tahini", LUNCH, "Baked falafel with tabbouleh, pickles and a tahini drizzle.", 540, 18, 62, 24),

                // Dinner
                meal("Baked cod with roasted vegetables", DINNER, "Lemon-herb cod fillet with roasted zucchini, peppers and onion.", 370, 34, 26, 14),
                meal("Chicken vegetable stir-fry", DINNER, "Chicken and colourful vegetables stir-fried in a light soy-ginger sauce.", 425, 36, 34, 16),
                meal("Turkey meatballs with zucchini noodles", DINNER, "Baked turkey meatballs in tomato sauce over zucchini noodles.", 400, 34, 18, 21),
                meal("Grilled steak with mixed salad", DINNER, "Lean sirloin steak with a large mixed salad and balsamic dressing.", 475, 42, 12, 29),
                meal("Chickpea vegetable curry with rice", DINNER, "Chickpeas and vegetables in a mild coconut curry with basmati rice.", 465, 15, 70, 14),
                meal("Shrimp tacos with slaw", DINNER, "Grilled shrimp in corn tortillas with crunchy cabbage slaw.", 415, 28, 40, 16),
                meal("Stuffed peppers with lean beef", DINNER, "Bell peppers stuffed with lean minced beef, rice and herbs.", 405, 30, 32, 17),
                meal("Chicken with molokhia and rice", DINNER, "Grilled chicken with traditional molokhia and a small portion of rice.", 445, 38, 44, 13),
                meal("Whole-wheat pasta primavera with chicken", DINNER, "Whole-wheat pasta with seasonal vegetables and grilled chicken.", 510, 36, 62, 13),

                // Snacks
                meal("Apple with almond butter", SNACK, "A crisp apple with a tablespoon of almond butter.", 205, 5, 25, 10),
                meal("Mixed nuts", SNACK, "A small handful of almonds, walnuts and cashews.", 185, 6, 7, 15),
                meal("Hummus with carrot sticks", SNACK, "Classic hummus with carrot and cucumber sticks.", 150, 5, 17, 7),
                meal("Protein shake with milk", SNACK, "Whey protein shaken with low-fat milk.", 220, 30, 14, 5),
                meal("Greek yogurt with honey", SNACK, "Plain Greek yogurt with a teaspoon of honey.", 150, 15, 18, 2),
                meal("Dates with a glass of milk", SNACK, "Three dates with a glass of low-fat milk.", 215, 9, 38, 3),
                meal("Two boiled eggs", SNACK, "Two hard-boiled eggs with a pinch of salt and pepper.", 140, 12, 1, 10),
                meal("Banana with peanut butter", SNACK, "A banana with a tablespoon of peanut butter.", 260, 7, 32, 12)
        );
        mealRepository.saveAll(meals);
        log.info("Seeded {} meals", meals.size());
    }

    private static Meal meal(String name, MealType type, String description,
                             int calories, int protein, int carbs, int fat) {
        return Meal.builder().name(name).mealType(type).description(description)
                .calories(calories).proteinG(protein).carbsG(carbs).fatG(fat).build();
    }
}
