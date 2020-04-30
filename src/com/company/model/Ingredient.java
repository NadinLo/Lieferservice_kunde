package com.company.model;

public class Ingredient {
    private int id;
    private String name;
    private boolean isVegetarian;
    private double singlePrice;

    public Ingredient(int id, String name, boolean isVegetarian, double singlePrice) {
        this.id = id;
        this.name = name;
        this.isVegetarian = isVegetarian;
        this.singlePrice = singlePrice;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }


    public double getSinglePrice() {
        return singlePrice;
    }

}
