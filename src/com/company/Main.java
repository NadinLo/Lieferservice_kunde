package com.company;

import com.company.controller.Controller;
import com.company.model.Order;

public class Main {
    //todo: calculating price does not work yet (resp. wrong)
    //todo: catch wrong entries

    public static void main(String[] args) {
        Controller controller = new Controller();

        System.out.println("Welcome to our delivery service!");
        controller.start(new Order());
    }
}
