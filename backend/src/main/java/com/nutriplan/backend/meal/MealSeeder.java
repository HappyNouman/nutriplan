package com.nutriplan.backend.meal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.nutriplan.backend.meal.IngredientCategory.*;
import static com.nutriplan.backend.meal.IngredientUnit.*;
import static com.nutriplan.backend.meal.MealType.*;

/**
 * Fills the meals table with starter data the first time the app runs (when the table is empty).
 * Each meal comes with its ingredients. Values are for ONE serving.
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
        List<Meal> meals = seedMeals();
        mealRepository.saveAll(meals);  // cascade also saves each meal's ingredients
        log.info("Seeded {} meals", meals.size());
    }

    /** Not private, so MealSeederTest can check the data. */
    static List<Meal> seedMeals() {
        return List.of(
                // Breakfast
                meal("Greek yogurt parfait", BREAKFAST, "Greek yogurt layered with mixed berries and a little granola.", 320, 20, 42, 8,
                        i("Greek yogurt", 200, GRAM, DAIRY), i("Mixed berries", 80, GRAM, PRODUCE), i("Granola", 30, GRAM, GRAINS)),
                meal("Veggie omelette with toast", BREAKFAST, "Two-egg omelette with spinach, tomato and peppers, with a slice of whole-wheat toast.", 345, 24, 26, 16,
                        i("Eggs", 2, PIECE, PROTEIN), i("Spinach", 30, GRAM, PRODUCE), i("Tomatoes", 80, GRAM, PRODUCE),
                        i("Bell peppers", 50, GRAM, PRODUCE), i("Whole-wheat bread (slices)", 1, PIECE, GRAINS)),
                meal("Banana peanut butter oatmeal", BREAKFAST, "Oats cooked in milk, topped with banana slices and a spoon of peanut butter.", 410, 13, 62, 12,
                        i("Rolled oats", 50, GRAM, GRAINS), i("Milk", 200, ML, DAIRY), i("Bananas", 1, PIECE, PRODUCE),
                        i("Peanut butter", 15, GRAM, PANTRY)),
                meal("Avocado toast with poached eggs", BREAKFAST, "Whole-grain toast with smashed avocado and two poached eggs.", 385, 17, 32, 21,
                        i("Whole-wheat bread (slices)", 2, PIECE, GRAINS), i("Avocados", 0.5, PIECE, PRODUCE), i("Eggs", 2, PIECE, PROTEIN)),
                meal("Foul medames with baladi bread", BREAKFAST, "Fava beans with olive oil, cumin and lemon, served with whole-wheat baladi bread.", 380, 19, 55, 9,
                        i("Fava beans (canned)", 200, GRAM, PANTRY), i("Olive oil", 10, ML, PANTRY), i("Lemons", 0.5, PIECE, PRODUCE),
                        i("Baladi bread", 1, PIECE, GRAINS)),
                meal("Cottage cheese fruit bowl", BREAKFAST, "Cottage cheese with pineapple, kiwi and a sprinkle of chia seeds.", 255, 24, 28, 5,
                        i("Cottage cheese", 200, GRAM, DAIRY), i("Pineapple", 80, GRAM, PRODUCE), i("Kiwis", 1, PIECE, PRODUCE),
                        i("Chia seeds", 10, GRAM, PANTRY)),
                meal("Protein pancakes with honey", BREAKFAST, "Oat and egg-white pancakes with a drizzle of honey and berries.", 420, 28, 54, 10,
                        i("Rolled oats", 50, GRAM, GRAINS), i("Eggs", 3, PIECE, PROTEIN), i("Honey", 15, GRAM, PANTRY),
                        i("Mixed berries", 50, GRAM, PRODUCE)),
                meal("Shakshuka with feta", BREAKFAST, "Eggs poached in a spiced tomato and pepper sauce, topped with feta.", 340, 19, 18, 21,
                        i("Eggs", 2, PIECE, PROTEIN), i("Tomatoes", 200, GRAM, PRODUCE), i("Bell peppers", 80, GRAM, PRODUCE),
                        i("Onions", 50, GRAM, PRODUCE), i("Feta cheese", 30, GRAM, DAIRY), i("Olive oil", 10, ML, PANTRY)),

                // Lunch
                meal("Grilled chicken quinoa bowl", LUNCH, "Grilled chicken breast over quinoa with cucumber, tomato and lemon dressing.", 510, 42, 48, 16,
                        i("Chicken breast", 150, GRAM, PROTEIN), i("Quinoa", 70, GRAM, GRAINS), i("Cucumbers", 80, GRAM, PRODUCE),
                        i("Tomatoes", 80, GRAM, PRODUCE), i("Lemons", 0.5, PIECE, PRODUCE), i("Olive oil", 10, ML, PANTRY)),
                meal("Turkey hummus wrap", LUNCH, "Whole-wheat wrap with turkey slices, hummus, lettuce and grated carrot.", 450, 32, 44, 16,
                        i("Whole-wheat wraps", 1, PIECE, GRAINS), i("Turkey slices", 100, GRAM, PROTEIN), i("Hummus", 40, GRAM, PANTRY),
                        i("Lettuce", 40, GRAM, PRODUCE), i("Carrots", 50, GRAM, PRODUCE)),
                meal("Lentil soup with salad", LUNCH, "Egyptian red lentil soup with a fresh green salad on the side.", 380, 20, 56, 8,
                        i("Red lentils", 80, GRAM, PANTRY), i("Onions", 50, GRAM, PRODUCE), i("Carrots", 50, GRAM, PRODUCE),
                        i("Lettuce", 60, GRAM, PRODUCE), i("Tomatoes", 80, GRAM, PRODUCE), i("Cucumbers", 80, GRAM, PRODUCE)),
                meal("Tuna chickpea salad", LUNCH, "Tuna, chickpeas, red onion, parsley and olive oil lemon dressing.", 410, 34, 30, 17,
                        i("Canned tuna", 120, GRAM, PROTEIN), i("Chickpeas (canned)", 120, GRAM, PANTRY), i("Red onions", 30, GRAM, PRODUCE),
                        i("Parsley", 15, GRAM, PRODUCE), i("Olive oil", 10, ML, PANTRY), i("Lemons", 0.5, PIECE, PRODUCE)),
                meal("Beef and broccoli with brown rice", LUNCH, "Lean beef strips stir-fried with broccoli, served with brown rice.", 550, 38, 58, 18,
                        i("Lean beef strips", 130, GRAM, PROTEIN), i("Broccoli", 150, GRAM, PRODUCE), i("Brown rice", 70, GRAM, GRAINS),
                        i("Soy sauce", 15, ML, PANTRY)),
                meal("Grilled salmon with sweet potato", LUNCH, "Salmon fillet with roasted sweet potato wedges and green beans.", 560, 36, 44, 26,
                        i("Salmon fillet", 150, GRAM, PROTEIN), i("Sweet potatoes", 200, GRAM, PRODUCE), i("Green beans", 100, GRAM, PRODUCE),
                        i("Olive oil", 10, ML, PANTRY)),
                meal("Chicken shawarma salad bowl", LUNCH, "Spiced chicken shawarma over salad with garlic yogurt sauce.", 480, 38, 30, 23,
                        i("Chicken breast", 150, GRAM, PROTEIN), i("Lettuce", 80, GRAM, PRODUCE), i("Tomatoes", 80, GRAM, PRODUCE),
                        i("Cucumbers", 80, GRAM, PRODUCE), i("Greek yogurt", 50, GRAM, DAIRY), i("Garlic (cloves)", 1, PIECE, PRODUCE)),
                meal("Light koshari", LUNCH, "A lighter portion of koshari with lentils, rice, pasta and spicy tomato sauce.", 520, 17, 92, 9,
                        i("Brown lentils", 50, GRAM, PANTRY), i("White rice", 50, GRAM, GRAINS), i("Pasta", 40, GRAM, GRAINS),
                        i("Tomato sauce", 100, GRAM, PANTRY), i("Onions", 50, GRAM, PRODUCE)),
                meal("Falafel bowl with tahini", LUNCH, "Baked falafel with tabbouleh, pickles and a tahini drizzle.", 540, 18, 62, 24,
                        i("Falafel mix", 100, GRAM, PANTRY), i("Tahini", 20, GRAM, PANTRY), i("Parsley", 20, GRAM, PRODUCE),
                        i("Tomatoes", 80, GRAM, PRODUCE), i("Bulgur", 40, GRAM, GRAINS), i("Pickles", 30, GRAM, PANTRY)),

                // Dinner
                meal("Baked cod with roasted vegetables", DINNER, "Lemon-herb cod fillet with roasted zucchini, peppers and onion.", 370, 34, 26, 14,
                        i("Cod fillet", 170, GRAM, PROTEIN), i("Zucchini", 150, GRAM, PRODUCE), i("Bell peppers", 80, GRAM, PRODUCE),
                        i("Onions", 50, GRAM, PRODUCE), i("Lemons", 0.5, PIECE, PRODUCE), i("Olive oil", 10, ML, PANTRY)),
                meal("Chicken vegetable stir-fry", DINNER, "Chicken and colourful vegetables stir-fried in a light soy-ginger sauce.", 425, 36, 34, 16,
                        i("Chicken breast", 150, GRAM, PROTEIN), i("Broccoli", 80, GRAM, PRODUCE), i("Bell peppers", 80, GRAM, PRODUCE),
                        i("Carrots", 50, GRAM, PRODUCE), i("Ginger", 5, GRAM, PRODUCE), i("Soy sauce", 15, ML, PANTRY)),
                meal("Turkey meatballs with zucchini noodles", DINNER, "Baked turkey meatballs in tomato sauce over zucchini noodles.", 400, 34, 18, 21,
                        i("Minced turkey", 150, GRAM, PROTEIN), i("Zucchini", 250, GRAM, PRODUCE), i("Tomato sauce", 120, GRAM, PANTRY)),
                meal("Grilled steak with mixed salad", DINNER, "Lean sirloin steak with a large mixed salad and balsamic dressing.", 475, 42, 12, 29,
                        i("Sirloin steak", 170, GRAM, PROTEIN), i("Lettuce", 100, GRAM, PRODUCE), i("Tomatoes", 100, GRAM, PRODUCE),
                        i("Cucumbers", 80, GRAM, PRODUCE), i("Balsamic vinegar", 15, ML, PANTRY), i("Olive oil", 10, ML, PANTRY)),
                meal("Chickpea vegetable curry with rice", DINNER, "Chickpeas and vegetables in a mild coconut curry with basmati rice.", 465, 15, 70, 14,
                        i("Chickpeas (canned)", 150, GRAM, PANTRY), i("Coconut milk", 80, ML, PANTRY), i("White rice", 60, GRAM, GRAINS),
                        i("Spinach", 50, GRAM, PRODUCE), i("Onions", 50, GRAM, PRODUCE), i("Curry paste", 15, GRAM, PANTRY)),
                meal("Shrimp tacos with slaw", DINNER, "Grilled shrimp in corn tortillas with crunchy cabbage slaw.", 415, 28, 40, 16,
                        i("Shrimp", 150, GRAM, PROTEIN), i("Corn tortillas", 3, PIECE, GRAINS), i("Cabbage", 100, GRAM, PRODUCE),
                        i("Limes", 0.5, PIECE, PRODUCE), i("Greek yogurt", 30, GRAM, DAIRY)),
                meal("Stuffed peppers with lean beef", DINNER, "Bell peppers stuffed with lean minced beef, rice and herbs.", 405, 30, 32, 17,
                        i("Bell peppers", 250, GRAM, PRODUCE), i("Minced beef", 120, GRAM, PROTEIN), i("White rice", 40, GRAM, GRAINS),
                        i("Tomato sauce", 80, GRAM, PANTRY), i("Parsley", 10, GRAM, PRODUCE)),
                meal("Chicken with molokhia and rice", DINNER, "Grilled chicken with traditional molokhia and a small portion of rice.", 445, 38, 44, 13,
                        i("Chicken breast", 150, GRAM, PROTEIN), i("Molokhia (frozen)", 200, GRAM, PRODUCE), i("White rice", 50, GRAM, GRAINS),
                        i("Garlic (cloves)", 2, PIECE, PRODUCE)),
                meal("Whole-wheat pasta primavera with chicken", DINNER, "Whole-wheat pasta with seasonal vegetables and grilled chicken.", 510, 36, 62, 13,
                        i("Whole-wheat pasta", 80, GRAM, GRAINS), i("Chicken breast", 120, GRAM, PROTEIN), i("Zucchini", 80, GRAM, PRODUCE),
                        i("Bell peppers", 80, GRAM, PRODUCE), i("Tomatoes", 80, GRAM, PRODUCE), i("Olive oil", 10, ML, PANTRY)),

                // Snacks
                meal("Apple with almond butter", SNACK, "A crisp apple with a tablespoon of almond butter.", 205, 5, 25, 10,
                        i("Apples", 1, PIECE, PRODUCE), i("Almond butter", 15, GRAM, PANTRY)),
                meal("Mixed nuts", SNACK, "A small handful of almonds, walnuts and cashews.", 185, 6, 7, 15,
                        i("Mixed nuts", 30, GRAM, PANTRY)),
                meal("Hummus with carrot sticks", SNACK, "Classic hummus with carrot and cucumber sticks.", 150, 5, 17, 7,
                        i("Hummus", 60, GRAM, PANTRY), i("Carrots", 80, GRAM, PRODUCE), i("Cucumbers", 80, GRAM, PRODUCE)),
                meal("Protein shake with milk", SNACK, "Whey protein shaken with low-fat milk.", 220, 30, 14, 5,
                        i("Whey protein", 30, GRAM, PANTRY), i("Milk", 250, ML, DAIRY)),
                meal("Greek yogurt with honey", SNACK, "Plain Greek yogurt with a teaspoon of honey.", 150, 15, 18, 2,
                        i("Greek yogurt", 170, GRAM, DAIRY), i("Honey", 7, GRAM, PANTRY)),
                meal("Dates with a glass of milk", SNACK, "Three dates with a glass of low-fat milk.", 215, 9, 38, 3,
                        i("Dates", 3, PIECE, PANTRY), i("Milk", 250, ML, DAIRY)),
                meal("Two boiled eggs", SNACK, "Two hard-boiled eggs with a pinch of salt and pepper.", 140, 12, 1, 10,
                        i("Eggs", 2, PIECE, PROTEIN)),
                meal("Banana with peanut butter", SNACK, "A banana with a tablespoon of peanut butter.", 260, 7, 32, 12,
                        i("Bananas", 1, PIECE, PRODUCE), i("Peanut butter", 15, GRAM, PANTRY))
        );
    }

    private static Meal meal(String name, MealType type, String description,
                             int calories, int protein, int carbs, int fat, MealIngredient... ingredients) {
        Meal meal = Meal.builder().name(name).mealType(type).description(description)
                .calories(calories).proteinG(protein).carbsG(carbs).fatG(fat).build();
        for (MealIngredient ingredient : ingredients) {
            meal.addIngredient(ingredient);
        }
        return meal;
    }

    /** Short name so the list above stays readable: i("Eggs", 2, PIECE, PROTEIN). */
    private static MealIngredient i(String name, double quantity, IngredientUnit unit, IngredientCategory category) {
        return new MealIngredient(name, quantity, unit, category);
    }
}
