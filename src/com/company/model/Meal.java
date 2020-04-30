package com.company.model;

import java.util.ArrayList;

public class Meal {
    private int id;
    private String name;
    private String menuType;
    private double menuPrice;
    private ArrayList<Ingredient> ingredients;

    private ArrayList<Ingredient> addIngredients = new ArrayList<>();
    private ArrayList<Ingredient> takeOffIngredients = new ArrayList<>();
    private boolean vegetarian = true;
    private int amount = 1;
    private double menuPriceInTotal;
    private int orderDetailsID;

    public Meal(int id, String name, String menuType, double menuPrice) {
        this.id = id;
        this.name = name;
        this.menuType = menuType;
        this.menuPrice = menuPrice;
        this.menuPriceInTotal = menuPrice;

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMenuType() {
        return menuType;
    }

    public double getMenuPrice() {
        return menuPrice;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<Ingredient> getAddIngredients() {
        return addIngredients;
    }

    public ArrayList<Ingredient> getTakeOffIngredients() {
        return takeOffIngredients;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getMenuPriceInTotal() {
        return menuPriceInTotal;
    }

    public int getOrderDetailsID() {
        return orderDetailsID;
    }

    public void setOrderDetailsID(int orderDetailsID) {
        this.orderDetailsID = orderDetailsID;
    }
}
