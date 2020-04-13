package com.company;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static DecimalFormat df = new DecimalFormat("##.##");
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int orderNo = 0;
        System.out.println("Welcome to our delivery service. Do you want to order something? (Y/N)");
        String decision = scanner.nextLine();
        if (decision.equalsIgnoreCase("Y")) {
            orderNo = startNewOrder();
        }
        if (decision.equalsIgnoreCase("N")) {
            System.out.println("Thank you for your visit and good bye");
            //todo: finish programm
        }
        //todo: Auswahl Menü anzeigen
        printMenu();
        //Menüwahl und Zutaten zufügen
        int menuNo = 0;
        int id = 0;
        decision = null;
        while (id == 0) {
            System.out.println("choose a menu. Enter the number");
            menuNo = scanner.nextInt();
            id = chooseMenu(menuNo);
            if (id == 0) {
                System.out.println("Something went wrong. Enter again a menu number");
            }
        }
        System.out.println("Do you want to change ingredients? (Y/N)");
        decision = scanner.next();
        if (decision.equalsIgnoreCase("Y")) {
            printIngredientList();
            ArrayList<Integer> addIngredients = new ArrayList<>();
            int ingredient = Integer.parseInt(null);
            System.out.println("Enter one after another all ingredients you wanna add to your menu.\n" +
                    "You can also enter ingredients you want to take off of the menu. Therefore you should mark" +
                    "the number with a \"-\". When you're finished enter \"0\".");
            while (ingredient != 0) {
                ingredient = scanner.nextInt();
                addIngredients.add(ingredient);
            }
            addIngredientToMenu(id, addIngredients);
        }

//todo: Zutaten ändern, weitere Menüs wählen bzw. Menenangaben machen
        //todo: Kundendaten eingeben
    }
    private static int startNewOrder () {
        Connection conn = null;
        int orderNo = 0;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            String command = "INSERT INTO `bestellung`(`ausgeliefert`) VALUES (null)";
            int ok = stmt.executeUpdate(command);
            String query = "SELECT Last_INSERT_ID()";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                orderNo = rs.getInt("Last_INSERT_ID()");
                System.out.println(orderNo);
            }
            if (ok == 1) {
                System.out.println("OK! You can place your order now.");
            } else {
                System.out.println("There might be a problem. Try again later");
            }
        } catch (SQLException ex) {
            throw new Error("Problem", ex);
        }
        return orderNo;
    }

    private static void printMenu() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            printType();
        } catch (SQLException ex) {
            throw new Error("Problem", ex);
        }
    }

    private static void printType() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            String queryType = "SELECT `id`, `name` FROM `menü_gruppe`";
            Statement stmtType = conn.createStatement();
            ResultSet rsType = stmtType.executeQuery(queryType);
            while (rsType.next()) {
                int typeId = rsType.getInt("id");
                String type = rsType.getString("name");
                System.out.println(type);
                printMenu(typeId);
            }
        } catch (SQLException ex) {
            throw new Error("Problem", ex);
        }
    }

    private static void printMenu(int typeId) {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            String queryMenu = "SELECT `menü_nr.`, `name`, `preis` FROM `menü` WHERE `menü_nr.` =" + typeId;
            Statement stmtMenu = conn.createStatement();
            ResultSet rsMenu = stmtMenu.executeQuery(queryMenu);
            //todo: springt nicht in die while-Schleife. Keine Fehlermeldung. Setzt Programm in Methode printType() fort
            while (rsMenu.next()) {
                int menuNo = rsMenu.getInt("menü_nr.");
                String menu = rsMenu.getString("name");
                double price = rsMenu.getDouble("preis");
                System.out.println("\t" + menuNo + ")\t" + menu + "\t\t" + df.format(price) + "€");
                printIngredients(menuNo);
            }
        } catch (SQLException ex) {
            throw new Error("Problem", ex);
        }
    }

    private static void printIngredients(int menuNo) {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            String queryIngr = "SELECT zutaten.name, zutaten.vegetarisch " +
                    "FROM `zutatenmix` " +
                    "Inner JOIN zutaten ON zutatenmix.zutaten_id = zutaten.id WHERE `menü_id` = " + menuNo;
            Statement stmtIngr = conn.createStatement();
            ResultSet rsIngr = stmtIngr.executeQuery(queryIngr);
            ArrayList<Integer> vegetarian = new ArrayList<>();
            int veggi = -1;
            while (rsIngr.next()) {
                String ingredient = rsIngr.getString("zutaten.name");
                veggi = rsIngr.getByte("zutaten.vegetarisch");
                System.out.print("\t\t" + ingredient + ", ");
                vegetarian.add(veggi);
            }
            if (!vegetarian.contains(0)) {
                System.out.println("\t\t\t\t(vegetarian)");
            }
        } catch (SQLException ex) {
            throw new Error("Problem", ex);
        }
    }

    private static int chooseMenu (int menuNo) {
        Connection conn = null;
        int id = 0;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            String command = "INSERT INTO `auswahl_details`(`menü_nr`) " +
                    "VALUES (" + menuNo + ")";
            stmt.executeUpdate(command);
            String query = "SELECT Last_INSERT_ID()";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                id = rs.getInt("Last_INSERT_ID()");
            }

        } catch (SQLException ex) {
            throw new Error("Problem", ex);
        }
        return id;
    }

    private static void printIngredientList () {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            String query = "SELECT * FROM `zutaten`";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.print(
                    "ALL INGREDIENTS-------------------------\n" +
                            "id\t| name\t\t\t | is veggi | price\n" +
                            "----------------------------------------\n");
            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                name = name.concat("               ");
                name = name.substring(0,15);
                boolean vegetarian = rs.getBoolean("vegetarisch");
                double price = rs.getDouble("preis");
                System.out.println(id + "\t| " + name + "| " + vegetarian + "  \t| " + price);
            }
            System.out.println("----------------------------------------\n");

        } catch (SQLException ex) {
            throw new Error("Problem", ex);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void addIngredientToMenu (int id, ArrayList<Integer> ingredients){

    }


}
