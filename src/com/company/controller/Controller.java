package com.company.controller;

import com.company.model.*;
import com.company.view.IngredientView;
import com.company.view.MealView;
import com.company.view.OrderView;
import com.company.view.UserView;

public class Controller {

    public void start(Order order) {
        OrderRepository orderRepository = new OrderRepository();
        OrderView orderView = new OrderView();
        MealView mealView = new MealView();
        MealRepository mealRepository = new MealRepository();
        IngredientRepository ingredientRepository = new IngredientRepository();
        IngredientView ingredientView = new IngredientView();
        UserView userView = new UserView();
        UserRepository userRepository = new UserRepository();

        if (orderRepository.create(order)) {
            User user = null;
            int choice = 0;
            while (choice != 6) {
                choice = orderView.mainMenu();
                //show menu
                if (choice == 1) {
                    //show all meals in menu
                    mealView.printMenu(mealRepository.findAll());
                }
                //choose a meal
                if (choice == 2) {
                    if( order.getChosenMeals().add(mealRepository.findOne(orderView.chooseMenu())) ){
                        orderView.showOrder(order);

                    }

                }
                //add or delete ingredients
                if (choice == 3) {
                    if (orderRepository.checkCurrentOrder(order)){
                        int orderNo = orderView.chooseOneOrderedMeal();
                        Meal meal = order.getChosenMeals().get(orderNo - 1);
                        ingredientView.printAllIngredients(ingredientRepository.findAll());
                        int ingredId = 1;
                        while (ingredId != 0) {
                            ingredId = orderView.changeIngredient();
                            if (ingredId > 0) {
                                //add ingredient
                                order.getChosenMeals().get(orderNo - 1).getAddIngredients().add(ingredientRepository.findOne(ingredId));
                            }
                            if (ingredId < 0) {
                                //delete ingredient
                                Ingredient ingredient = ingredientRepository.findOne(-ingredId);
                                boolean ingredientIsOnMeal = false;
                                for (Ingredient ingredient1 : meal.getIngredients()) {
                                    if (ingredient.getId() == ingredient1.getId()) {
                                        order.getChosenMeals().get(orderNo -1).getTakeOffIngredients().add(ingredient);
                                        ingredientIsOnMeal = true;
                                    }
                                }
                                if (!ingredientIsOnMeal) {
                                    System.out.println("This ingredient is not on your meal so you cannot take it off.");
                                }
                            }
                        }
                        orderView.showOrder(order);
                    }
                }
                //change amount
                if (choice == 4) {
                    if (orderRepository.checkCurrentOrder(order)) {
                        int orderNo = orderView.chooseOneOrderedMeal();
                        order.getChosenMeals().get(orderNo - 1).setAmount(orderView.changeAmount());
                        orderView.showOrder(order);
                    }
                }
                //finish order
                if (choice == 5) {
                    if(orderRepository.checkCurrentOrder(order)) {
                        //Customer data
                        user = new User(userView.getCustomerName(),
                                userView.getCustomerAddress(), userView.getZIP(), userView.getCustomerLocation());
                        //is location delivered
                        if (userRepository.isLocationInsideDeliveryArea(user)) {
                            user.setOrderNo(order.getOrderNo());
                            orderRepository.calculateDeliveryZone(order, user.getLocationId());
                            //confirm order
                            if (orderView.orderOverview(order)) {
                                //send order and customer data
                                if (orderRepository.updateOrderDetails(order) && userRepository.create(user)) {
                                    //update status of order to 'in process'
                                    orderRepository.updateOrderStatus(order.getOrderNo());
                                    //everything was successful, finish program
                                    orderView.SendOrderSuccessful();
                                    System.exit(0);
                                } else {
                                    orderView.ProblemWithSendingOrder();
                                }
                            }
                        }
                    }
                }
                //quit program
                if (choice == 6) {
                    if (order != null){
                        if (order.getChosenMeals().size() > 0){
                            for (int i = 0; i < order.getChosenMeals().size(); i++) {
                                if (order.getChosenMeals().get(i).getAddIngredients().size() > 0 ||
                                        order.getChosenMeals().get(i).getTakeOffIngredients().size() > 0){
                                    mealRepository.deleteOrderDetails(order);
                                }
                            }
                            orderRepository.deleteChosenMeals(order);
                        }
                        if (user != null){
                            userRepository.deleteCustomerData(order);
                        }
                        orderRepository.deleteOrder(order);
                        order = null;
                    }
                }
            }
        }
    }
}
