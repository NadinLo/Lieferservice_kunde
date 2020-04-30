package com.company.view;

import java.util.Scanner;

public class UserView {
    Scanner scannerForString = new Scanner(System.in);

    public String getCustomerName () {
        System.out.println("We need now the delivery data. Please enter your name.");
        return scannerForString.nextLine();
    }

    public String getCustomerAddress () {
        System.out.println("Please enter your address (Street and Number).");
        return scannerForString.nextLine();
    }

    public String getZIP () {
        System.out.println("Please enter the ZIP code of your home.");
        return scannerForString.nextLine();
    }

    public String getCustomerLocation () {
        System.out.println("Please enter the name of location where you live.");
        return scannerForString.nextLine();
    }
}
