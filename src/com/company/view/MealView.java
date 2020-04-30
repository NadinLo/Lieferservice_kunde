package com.company.view;

import com.company.model.Ingredient;
import com.company.model.Meal;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MealView {
    DecimalFormat df = new DecimalFormat("##.##");

    private void printHeader (){
        System.out.println("_Meal_type_____|_Meal_No_|_Meal________________|_Vegi_|_Price______");
    }

    public void printMenu (ArrayList<Meal> menu){
        printHeader();
        for (int i = 0; i < menu.size(); i++) {
            if (i > 0 && menu.get(i).getMenuType().equalsIgnoreCase(menu.get(i-1).getMenuType())){
                System.out.println("               | " + (menu.get(i).getId() + "      ").substring(0,8) +
                        "| " + (menu.get(i).getName() + "                      ").substring(0,20) +
                        "| " + (menu.get(i).isVegetarian() + "          ").substring(0,5) +
                        "| " + (df.format(menu.get(i).getMenuPrice()) + " €"));
            } else {
                System.out.println(" " + (menu.get(i).getMenuType() + "          ").substring(0,14) +
                        "| " + (menu.get(i).getId() + "      ").substring(0,8) +
                        "| " + (menu.get(i).getName() + "                      ").substring(0,20) +
                        "| " + (menu.get(i).isVegetarian() + "          ").substring(0,5) +
                        "| " + (df.format(menu.get(i).getMenuPrice()) + " €"));
            }
            printIngredientsMenu(menu.get(i).getIngredients());
        }
    }

    private void printIngredientsMenu (ArrayList<Ingredient> ingredients){
        for (Ingredient ingredient : ingredients) {
            System.out.println("               |         |" +
                    " - " + (ingredient.getName() + "                     ").substring(0, 18) + "|      |            ");
        }
    }



}
