package com.company.view;

import com.company.model.Order;
import java.text.DecimalFormat;
import java.util.Scanner;

public class OrderView {
    Scanner scannerForInt = new Scanner(System.in);
    Scanner scannerForString = new Scanner(System.in);
    DecimalFormat df = new DecimalFormat("##.##");

    public int mainMenu() {
        System.out.println("You have now following possibilities: \n");

        System.out.println("1) show whole menu");
        System.out.println("2) choose a meal");
        System.out.println("3) add ingredients to or take off from your chosen meal");
        System.out.println("4) change the amounts of your ordered meals");
        System.out.println("5) finish your order and continue with the customer details");
        System.out.println("6) quit the program without sending any order");
        System.out.println("\nenter the program number: ");

        return scannerForInt.nextInt();
    }

    public int chooseMenu() {
        System.out.println("Enter the number of the meal you like to choose.");
        return scannerForInt.nextInt();
    }

    public void showOrder(Order order) {
        //"   4 |                     40                  |                |             "
        for (int i = 0; i < order.getChosenMeals().size(); i++) {
            System.out.println(" " + ((i+1) + "   ").substring(0,4) + "| " +
                    (order.getChosenMeals().get(i).getMenuType() + " " +
                            order.getChosenMeals().get(i).getName() + " (No " + order.getChosenMeals().get(i).getId() + ")                                               ").substring(0,40) +
                    "| " + (df.format(order.getChosenMeals().get(i).getMenuPrice()) + " €            ").substring(0,15) +
                    "| " + order.getChosenMeals().get(i).getAmount() + "x");
            for (int j = 0; j < order.getChosenMeals().get(i).getAddIngredients().size(); j++) {
                System.out.println("     | " + ("with extra: " + order.getChosenMeals().get(i).getAddIngredients().get(j).getName() + "                              ").substring(0,39) +
                        " | " + ("+ " + df.format(order.getChosenMeals().get(i).getAddIngredients().get(j).getSinglePrice()) + " €           ").substring(0,14) +
                        " |");

            }
            for (int j = 0; j < order.getChosenMeals().get(i).getTakeOffIngredients().size(); j++) {
                System.out.println("     | " + ("without: " + order.getChosenMeals().get(i).getTakeOffIngredients().get(j).getName()+ "                              ").substring(0,39) +
                        " | " + ("- " + df.format(order.getChosenMeals().get(i).getTakeOffIngredients().get(j).getSinglePrice()) + " €           ").substring(0,14) +
                        " |");

            }
            System.out.println("     |                                         | meal in total: " +
                    df.format(order.getChosenMeals().get(i).getMenuPriceInTotal()) + " €");



        }
        System.out.println("\n     |                                         | order in total: " +
                df.format(order.getPriceInTotal()) + " €");

    }

    public int chooseOneOrderedMeal (){
        System.out.println("Which meal do you want to change? enter the number of your order.");
        return scannerForInt.nextInt();
    }

    public int changeIngredient (){
        System.out.println("Enter one ingredient you would like to add to your meal.\n" +
                "You can also enter one ingredient you want to take off of the meal. \n" +
                "Therefore mark the number with a '-'. When you're finished enter '0'.");
        return scannerForInt.nextInt();
    }

    public int changeAmount (){
        System.out.println("Enter how many of the chosen meal you would like to order.");
        return scannerForInt.nextInt();
    }

    public void SendOrderSuccessful () {
        System.out.println("Your Order was send successfully. We hope you'll enjoy your meal and that we'll see you again soon.");
    }

    public void ProblemWithSendingOrder (){
        System.out.println("There is a problem with sending your order.");
    }

    public boolean orderOverview (Order order) {
        showOrder(order);
        System.out.println("Delivery zone: " + order.getDeliveryZone());
        System.out.println("Delivery fee: " + df.format(order.getDeliveryFee()) + " €");
        //System.out.println("Delivery can be expected in " + order.getMinutesToDeliver() + " minutes");   //+ 30 Minutes to prepare Meal

        System.out.println("\nAre you ok with your order? Enter 'Y' for yes to send the order. Enter 'N' to quit the program");
        return scannerForString.nextLine().equalsIgnoreCase("y");
    }


}
