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
        //todo: Auswahl Menü anzeigen: funktioniert noch nicht.
        printMenu();

        //Menüwahl und Zutaten zufügen/entfernen
        int menuNo = 0;
        int id = 0;
        decision = null;
        System.out.println("choose a menu. Enter the number");
        menuNo = scanner.nextInt();
        id = chooseMenu(orderNo, menuNo);
        if (id == 0) {
            System.out.println("Something went wrong. Enter again a menu number");
        }
        System.out.println("Do you want to change ingredients? (Y/N)");
        decision = scanner.next();
        if (decision.equalsIgnoreCase("Y")) {
            printIngredientList();
            ArrayList<Integer> addIngredients = new ArrayList<>();
            int ingredient = 1;
            System.out.println("Enter one after another all ingredients you wanna add to your menu.\n" +
                    "You can also enter ingredients you want to take off of the menu. Therefore you should mark" +
                    "the number with a \"-\". When you're finished enter \"0\".");
            while (ingredient != 0) {
                ingredient = scanner.nextInt();
                if (ingredient != 0) {
                    addIngredients.add(ingredient);
                }
            }
            addIngredientToMenu(id, addIngredients);
            printEditedMenu(id);
        }
        System.out.println("Would you like to change the amount of this menu? (Y/N)");
        decision = scanner.next();
        if (decision.equalsIgnoreCase("Y")) {
            System.out.println("How many times you want to order this menu? Just enter the number.");
            int amount = scanner.nextInt();
            changeAmount (id, amount);
        }

//todo: weitere Menüs wählen bzw. Menenangaben machen
        //todo: Kundendaten eingeben
    }

    private static int startNewOrder() {
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
            while (rs.next()) {
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
            String queryType = "SELECT `id`, `name` FROM `menu_gruppe`";
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
            String queryMenu = "SELECT `menu_nr`, `name`, `preis` FROM `menu` WHERE `menu_nr` =" + typeId;
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
                    "Inner JOIN zutaten ON zutatenmix.zutaten_id = zutaten.id WHERE `menu_id` = " + menuNo;
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

    private static int chooseMenu(int orderNo, int menuNo) {
        Connection conn = null;
        int id = 0;
        int ok = 0;

        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt = null;
            String command = "INSERT INTO `menu_auswahl`(`bestell_nr`, `menu_nr`, anzahl) " +
                    "VALUES (" + orderNo + ", " + menuNo + ", 1)";

            stmt = conn.createStatement();
            ok = stmt.executeUpdate(command);
            String query = "SELECT Last_INSERT_ID()";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                id = rs.getInt("Last_INSERT_ID()");
            }


        } catch (SQLException ex) {
            throw new Error("Problem", ex);
        }
        return id;
    }

    private static void printIngredientList() {
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
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                name = name.concat("               ");
                name = name.substring(0, 15);
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

    private static void addIngredientToMenu(int id, ArrayList<Integer> ingredients) {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            String command = "";
            for (int ingredient : ingredients) {
                if (ingredient > 0) {
                    command = "INSERT INTO `zutaten_hinzuf`(`id_detail_auswahl`, `zutaten_id`) " +
                            "VALUES (" + id + "," + ingredient + ")";
                }
                else if (ingredient < 0) {
                    ingredient = -ingredient;
                    command = "INSERT INTO `zutaten_entfernen`(`id_detail_auswahl`, `zutaten_id`) " +
                            "VALUES (" + id + "," + ingredient + ")";
                }
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(command);
            }
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

    private static void printEditedMenu(int id) {
        Connection conn = null;
        double price = 0;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt = null;
            System.out.println("your chosen menu: ");
            String query = "SELECT menu_auswahl.menu_nr, menu.name, menu.preis " +
                    "FROM `menu_auswahl` " +
                    "INNER JOIN menu ON menu_auswahl.menu_nr = menu.menu_nr " +
                    "WHERE menu_auswahl.id_detail_auswahl = " + id;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                int menuNo = rs.getInt("menu_auswahl.menu_nr");
                String menuName = rs.getString("menu.name");
                price = rs.getDouble("menu.preis");
                System.out.println(menuNo + ") " + menuName + ": " + df.format(price) + " €");

                query = "SELECT zutaten.name " +
                        "FROM zutatenmix " +
                        "INNER JOIN zutaten ON zutatenmix.zutaten_id = zutaten.id WHERE zutatenmix.menü_id = " + menuNo;
                rs = stmt.executeQuery(query);
                System.out.print("\t(");
                while (rs.next()){
                    String ingredientName = rs.getString("zutaten.name");
                    System.out.print(ingredientName + ", ");
                }
                System.out.println(")");
            }
            query = "SELECT zutaten.name, zutaten.preis " +
                    "FROM `zutaten_hinzuf` " +
                    "INNER JOIN zutaten ON zutaten_hinzuf.zutaten_id = zutaten.id " +
                    "WHERE id_detail_auswahl = " + id;
            rs = stmt.executeQuery(query);
            System.out.println("With extra:");
            while (rs.next()){
                String addedIngredient = rs.getString("zutaten.name");
                double ingrPrice = rs.getDouble("zutaten.preis");
                System.out.println("\t" + addedIngredient + " + " + df.format(ingrPrice) + " €");
                price = price + ingrPrice;
            }
            query = "SELECT zutaten.name, zutaten.preis " +
                    "FROM `zutaten_entfernen` " +
                    "INNER JOIN zutaten ON zutaten_entfernen.zutaten_id = zutaten.id " +
                    "WHERE id_detail_auswahl = " + id;
            rs = stmt.executeQuery(query);
            System.out.println("Without: ");
            while (rs.next()){
                String deletedIngredient = rs.getString("zutaten.name");
                double ingrPrice = rs.getDouble("zutaten.preis");
                System.out.println("\t" + deletedIngredient + " - " + df.format(ingrPrice) + " €");
                price = price - ingrPrice;
            }
            System.out.println("price in total: " + df.format(price) + "€ ");

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

    private static void changeAmount (int id, int amount){
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt = null;
            String command = "UPDATE `menu_auswahl` SET `anzahl`= " + amount + " WHERE `id_detail_auswahl` = " + id;
            stmt = conn.createStatement();
            stmt.executeUpdate(command);

        } catch (SQLException ex){
            throw new Error("Problem", ex);
        }
    }


}
