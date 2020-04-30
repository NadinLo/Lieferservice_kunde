package com.company.model;

import java.util.ArrayList;

public class Order {
    private int orderNo;
    private ArrayList<Meal> chosenMeals = new ArrayList<>();
    private double priceInTotal = getPriceInTotal();
    private int deliveryZone;
    private double deliveryFee;


    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getOrderStatus() {
        // 0 = not in process yet / 1 = in process but not delivered yet /  2 = delivered
        return 0;
    }

    public ArrayList<Meal> getChosenMeals() {
        return chosenMeals;
    }

    public double getPriceInTotal() {
        for (Meal chosenMeal : this.chosenMeals) {
            priceInTotal = priceInTotal + chosenMeal.getMenuPriceInTotal();
        }
        return priceInTotal;
    }

    public int getDeliveryZone() {
        return deliveryZone;
    }

    public void setDeliveryZone(int deliveryZone) {
        this.deliveryZone = deliveryZone;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

}
