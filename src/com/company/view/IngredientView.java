package com.company.view;

import com.company.model.Ingredient;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class IngredientView {
    DecimalFormat df = new DecimalFormat("##.##");

    public void printAllIngredients (ArrayList<Ingredient> ingredients){
        System.out.println("ALL INGREDIENTS----------------------------------------");
        System.out.println("_id_|_name__________________|_is_veggi_|_price_________");
        for (Ingredient ingredient : ingredients) {
            System.out.println((" " + ingredient.getId() + "   ").substring(0, 4) + "| " +
                    (ingredient.getName() + "                      ").substring(0, 22) + "| " +
                    (ingredient.isVegetarian() + "         ").substring(0, 9) + "| " +
                    df.format(ingredient.getSinglePrice()) + " €");
        }
        System.out.println("------------------------------------------------\n");
    }
}
