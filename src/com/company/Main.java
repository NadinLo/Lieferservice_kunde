package com.company;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static DecimalFormat df = new DecimalFormat("##.##");
    private static Scanner scannerForInt = new Scanner(System.in);
    private static Scanner scannerForString = new Scanner(System.in);

    public static void main(String[] args) {
        int orderNo = 0;
        System.out.println("Welcome to our delivery service. Do you want to order something? (Y/N)");
        String decision = scannerForString.nextLine();
        if (decision.equalsIgnoreCase("Y")) {
            orderNo = startNewOrder();
        }
        if (decision.equalsIgnoreCase("N")) {
            System.out.println("Thank you for your visit and good bye");
            System.exit(0);
        }
        while (decision.equalsIgnoreCase("Y")) {
            System.out.println("Here you are the menu: ");
            printMenu();

//Choose menu:
            int menuNo;
            int id;
            System.out.println("choose a menu. Enter the number");
            menuNo = scannerForInt.nextInt();
            id = chooseMenu(orderNo, menuNo);
            if (id == 0) {
                System.out.println("Something went wrong. Enter again a menu number");
            }
//Change ingredients:
            System.out.println("Do you want to change ingredients? (Y/N)");
            decision = scannerForString.nextLine();
            if (decision.equalsIgnoreCase("Y")) {
                printIngredientList();
                ArrayList<Integer> addIngredients = new ArrayList<>();
                int ingredient = 1;
                System.out.println("Enter one after another all ingredients you wanna add to your menu.\n" +
                        "You can also enter ingredients you want to take off of the menu. Therefore you should mark" +
                        "the number with a \"-\". When you're finished enter \"0\".");
                while (ingredient != 0) {
                    ingredient = scannerForInt.nextInt();
                    if (ingredient != 0) {
                        addIngredients.add(ingredient);
                    }
                }
                addIngredientToMenu(id, addIngredients);
                printEditedMenu(id);
            }
//Change Amount:
            System.out.println("Would you like to change the amount of this menu? (Y/N)");
            decision = scannerForString.nextLine();
            if (decision.equalsIgnoreCase("Y")) {
                System.out.println("How many times you want to order this menu? Just enter the number.");
                int amount = scannerForInt.nextInt();
                changeAmount(id, amount);
            }

//add more menus:
            System.out.println("do you want to add more menus? (Y/N)");
            decision = scannerForString.nextLine();
        }
//enter customer Dates:
        System.out.print("enter the following customer data.");
        System.out.print("\nName: ");
        String customerName = scannerForString.nextLine();
        System.out.print("\nStreet and number: ");
        String address = scannerForString.nextLine();
        System.out.print("\nVillage/ town: ");
        String location = scannerForString.nextLine();
        enterCustomerData(orderNo, customerName, address, location);

//FINISHED ORDER => OVERVIEW:
        overview(orderNo);
    }

    private static int startNewOrder() {
        Connection conn = null;
        int orderNo = 0;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            String command = "INSERT INTO `bestellung`(abgeschlossen) VALUES (0)";
            int ok = stmt.executeUpdate(command);
            String query = "SELECT Last_INSERT_ID()";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                orderNo = rs.getInt("Last_INSERT_ID()");
            }
            if (ok == 1) {
                System.out.println("OK! You can place your order now.");
            } else {
                System.out.println("There might be a problem. Try again later");
            }
        } catch (SQLException ex) {
            throw new Error("Problem", ex);
        }  finally {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
        return orderNo;
    }

    private static void printMenu() {
        ArrayList<String> menyTypes = new ArrayList<>();
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            //getMenuType
            try {
                String query = "SELECT * FROM `menu_gruppe`";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    menyTypes.add(rs.getString("name"));
                }
            } catch (SQLException ex){
                throw new Error("something went wrong with getMenuType", ex);
            }
            //getMenus
            System.out.println("___________________________________________");
            try {
                for (String menyType : menyTypes) {
                    System.out.println("=> " + menyType + ": ");
                    String query = "SELECT * " +
                            "FROM menu " +
                            "INNER JOIN menu_gruppe ON menu.menu_gruppe = menu_gruppe.id " +
                            "WHERE menu_gruppe.name = '" + menyType + "'";
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        int menuNo = rs.getInt("menu_nr");
                        String menuName = rs.getString("name");
                        double menuPrice = rs.getDouble("preis");

                        System.out.println("\t" + menuNo + ")\t" + menuName + "\t[" + df.format(menuPrice) + "€]");
                        System.out.print("\t\twith: ");
                        //getIngredientsOfMenu
                        ArrayList<Boolean> isIngredVeggi = new ArrayList<>();
                        try {
                            Statement subStmt = conn.createStatement();
                            String subQuery = "SELECT zutaten.name, zutaten.vegetarisch " +
                                    "FROM zutatenmix " +
                                    "INNER JOIN zutaten ON zutatenmix.zutaten_id = zutaten.id " +
                                    "WHERE zutatenmix.menü_id = " + menuNo;
                            ResultSet subRs = subStmt.executeQuery(subQuery);
                            while (subRs.next()) {
                                String ingredName = subRs.getString("name");
                                isIngredVeggi.add(subRs.getBoolean("vegetarisch"));
                                System.out.print(ingredName + ", ");
                            }
                            if (!isIngredVeggi.contains(false)) {
                                System.out.println("\n\t\t(vegetarian)");
                            } else {
                                System.out.println();
                            }
                        } catch (SQLException ex) {
                            throw new Error("something went wrong with getIngredientsOfMenu", ex);
                        }
                    }
                }
                System.out.println("___________________________________________");
            } catch (SQLException ex){
                throw new Error("something went wrong with getMenus", ex);
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

    private static int chooseMenu(int orderNo, int menuNo) {
        Connection conn = null;
        int id = 0;

        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt;
            String command = "INSERT INTO `menu_auswahl`(`bestell_nr`, `menu_nr`, anzahl) " +
                    "VALUES (" + orderNo + ", " + menuNo + ", 1)";

            stmt = conn.createStatement();
            stmt.executeUpdate(command);
            String query = "SELECT Last_INSERT_ID()";
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                id = rs.getInt("Last_INSERT_ID()");
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
                } else if (ingredient < 0) {
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
            Statement stmt;
            System.out.println("your chosen menu: ");
            String query = "SELECT menu_auswahl.menu_nr, menu.name, menu.preis " +
                    "FROM `menu_auswahl` " +
                    "INNER JOIN menu ON menu_auswahl.menu_nr = menu.menu_nr " +
                    "WHERE menu_auswahl.id_detail_auswahl = " + id;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int menuNo = rs.getInt("menu_auswahl.menu_nr");
                String menuName = rs.getString("menu.name");
                price = rs.getDouble("menu.preis");
                System.out.println(menuNo + ") " + menuName + ": " + df.format(price) + " €");

                query = "SELECT zutaten.name " +
                        "FROM zutatenmix " +
                        "INNER JOIN zutaten ON zutatenmix.zutaten_id = zutaten.id WHERE zutatenmix.menü_id = " + menuNo;
                rs = stmt.executeQuery(query);
                System.out.print("\t(");
                while (rs.next()) {
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
            while (rs.next()) {
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
            while (rs.next()) {
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

    private static void changeAmount(int id, int amount) {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt;
            String command = "UPDATE `menu_auswahl` SET `anzahl`= " + amount + " WHERE `id_detail_auswahl` = " + id;
            stmt = conn.createStatement();
            stmt.executeUpdate(command);

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

    private static void enterCustomerData(int orderNo, String name, String address, String location) {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt;
            String query = "SELECT id, name FROM belieferte_ortschaften";
            stmt = conn.createStatement();
            int locationID = 0;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                locationID = rs.getInt("id");
                String locationName = rs.getString("name");
                if (!locationName.equalsIgnoreCase(location)) {
                    locationID = 0;
                } else {
                    break;
                }
            }
            if (locationID != 0) {
                String command = "INSERT INTO `kunde`(`bestellnr`, `name`, `straße_hnr`, `ortschaft`) " +
                        "VALUES (" + orderNo + ", '" + name + "', '" + address + "', " + locationID + ")";
                stmt = conn.createStatement();
                stmt.executeUpdate(command);
                //calculateDeliveryArea(orderNo);
            } else {
                System.out.println("We don't deliver to this area. Sorry.");
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

    private static double calculateDeliveryArea(int orderNo) {
        Connection conn = null;
        double deliveryFee = 0;
        int deliveryArea = 0;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt;
            String query = "SELECT lieferzone.id, lieferzone.lieferpreis " +
                    "FROM lieferzone " +
                    "WHERE lieferzone.id = (SELECT lieferzone.id " +
                    "FROM lieferzone " +
                    "WHERE lieferzone.distance_min <= " +
                    "(SELECT belieferte_ortschaften.distance " +
                    "FROM belieferte_ortschaften " +
                    "INNER JOIN kunde ON kunde.ortschaft = belieferte_ortschaften.id WHERE kunde.bestellnr = " + orderNo + ") " +
                    "AND lieferzone.distance_max > " +
                    "(SELECT belieferte_ortschaften.distance " +
                    "FROM belieferte_ortschaften " +
                    "INNER JOIN kunde ON kunde.ortschaft = belieferte_ortschaften.id WHERE kunde.bestellnr = " + orderNo + "))";
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                deliveryArea = rs.getInt("lieferzone.id");
                deliveryFee = rs.getDouble("lieferzone.lieferpreis");
            }
            System.out.println("Area " + deliveryArea + "\nPrice for delivery: " + df.format(deliveryFee) + "€");


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
        return deliveryFee;
    }

    private static void overview(int orderNo) {
        double priceInTotal = 0;
        System.out.println("YOUR ORDER\t\t\t" + orderNo);
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt;
//kundendaten
            String customerName = null;
            String address = null;
            String ZIP = null;
            String location = null;
            try {
                String query = "SELECT kunde.name AS 'Kundenname', " +
                        "kunde.straße_hnr as 'Straße Hausnummer', " +
                        "belieferte_ortschaften.plz as 'PLZ', " +
                        "belieferte_ortschaften.name AS 'Ort' " +
                        "FROM `kunde` " +
                        "INNER JOIN belieferte_ortschaften ON kunde.ortschaft = belieferte_ortschaften.id " +
                        "WHERE `bestellnr` = " + orderNo;
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    customerName = rs.getString("Kundenname");
                    address = rs.getString("Straße Hausnummer");
                    ZIP = "" + rs.getInt("PLZ");
                    location = rs.getString("Ort");
                }
                printCustomerData (customerName, address, ZIP, location);
            } catch (SQLException ex) {
                System.out.println("something went wrong with the customerData");
            }
//komplette Bestellung
            //Detail-Nummer
            ArrayList<Integer> detailID = new ArrayList<>();
            try {
                String query = "SELECT menu_auswahl.id_detail_auswahl " +
                        "FROM menu_auswahl " +
                        "WHERE menu_auswahl.bestell_nr = " + orderNo;
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    detailID.add(rs.getInt("menu_auswahl.id_detail_auswahl"));
                }
            } catch (SQLException ex) {
                throw new Error("something went wrong with you id_ArrayList", ex);
            }
            for (Integer integer : detailID) {
                //Menünummer, Menüname, Gruppen_nr., Menüpreis, Menge
                int menuNo = 0;
                String menuName = "";
                int typeID = 0;
                double menuPrice = 0;
                int amount = 0;
                try {
                    String query = "SELECT menu_auswahl.menu_nr, " +
                            "menu.name, " +
                            "menu.menu_gruppe, " +
                            "menu.preis, " +
                            "menu_auswahl.anzahl " +
                            "FROM menu_auswahl " +
                            "INNER JOIN menu ON menu_auswahl.menu_nr = menu.menu_nr " +
                            "WHERE menu_auswahl.id_detail_auswahl = " + integer;

                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        menuNo = rs.getInt("menu_auswahl.menu_nr");
                        menuName = rs.getString("menu.name");
                        typeID = rs.getInt("menu.menu_gruppe");
                        menuPrice = rs.getDouble("menu.preis");
                        amount = rs.getInt("menu_auswahl.anzahl");
                    }
                } catch (SQLException ex) {
                    throw new Error("something went wrong with the ordered menu", ex);
                }
                //Details:
                ArrayList<String> extraIngr = new ArrayList<>();
                ArrayList<Double> extraIngrPrice = new ArrayList<>();
                ArrayList<String> deleteIngr = new ArrayList<>();
                ArrayList<Double> deleteIngrPrice = new ArrayList<>();

                try {//Details: extra Zutaten
                    String query = "SELECT zutaten.name AS 'Zutat', zutaten.preis AS 'Preis' " +
                            "FROM zutaten_hinzuf " +
                            "INNER JOIN zutaten ON zutaten_hinzuf.zutaten_id = zutaten.id " +
                            "WHERE zutaten_hinzuf.id_detail_auswahl = " + integer;
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        extraIngr.add(rs.getString("Zutat"));
                        extraIngrPrice.add(rs.getDouble("Preis"));
                    }
                } catch (SQLException ex) {
                    System.out.println("something went wrong with extra Ingredients");
                    throw new Error(ex);
                }

                try {//Details: Zutaten weglassen
                    String query = "SELECT zutaten.name, zutaten.preis " +
                            "FROM zutaten_entfernen " +
                            "INNER JOIN zutaten ON zutaten_entfernen.zutaten_id = zutaten.id " +
                            "WHERE zutaten_entfernen.id_detail_auswahl = " + integer;
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        deleteIngr.add(rs.getString("zutaten.name"));
                        deleteIngrPrice.add(rs.getDouble("zutaten.preis"));
                    }
                } catch (SQLException ex) {
                    System.out.println("something went wrong with the deleted ingredients");
                }

                //Menügruppenname
                String menuType = "";
                try {
                    String query = "SELECT menu_gruppe.name FROM `menu_gruppe` WHERE menu_gruppe.id = " + typeID;
                    ResultSet rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        menuType = rs.getString("menu_gruppe.name");
                    }

                } catch (SQLException ex) {
                    throw new Error("something went wrong with the menu_type", ex);
                }
                priceInTotal = priceInTotal + printMenuII(menuNo, menuName, menuType, menuPrice, extraIngr,
                        extraIngrPrice, deleteIngr, deleteIngrPrice, amount);
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
        //Lieferzone, Lieferpreis
        priceInTotal = priceInTotal + calculateDeliveryArea(orderNo);
        //gesamtpreis
        System.out.println("=== To pay: " + df.format(priceInTotal) + "€ ===");
    }

    private static double printMenuII (int menuNo, String menuName, String menuType, double menuPrice,
                                     ArrayList<String> extraIngr , ArrayList<Double> extraIngrPrice,
                                     ArrayList<String> deleteIngr, ArrayList<Double> deleteIngrPrice,
                                     int amount) {
        System.out.println("MENU  NO. " + menuNo);
        System.out.println("\t" + menuName + " (" + menuType + ")\t" + df.format(menuPrice) + "€");
        if(extraIngr.size()>0) {
            System.out.println("\twith extra:");
            for (int i = 0; i < extraIngr.size(); i++) {
                System.out.println("\t\t" + extraIngr.get(i) + " + " + df.format(extraIngrPrice.get(i)) + "€");
                menuPrice = menuPrice + extraIngrPrice.get(i);
            }
        }
        if (deleteIngr.size()>0) {
            System.out.println("\twithout:");
            for (int i = 0; i < deleteIngr.size(); i++) {
                System.out.println("\t\t" + deleteIngr.get(i) + " - " + df.format(deleteIngrPrice.get(i)) + "€");
                menuPrice = menuPrice - deleteIngrPrice.get(i);
            }
        }
        menuPrice = menuPrice * amount;
        System.out.println("\t_____________________");
        System.out.println("\t" + amount + "x \t\t\t\t= " + df.format(menuPrice) + "€");
        return menuPrice;

    }

    private static void printCustomerData (String customerName, String address, String ZIP, String location){
        System.out.println("CUSTOMER DETAILS");
        System.out.println(customerName);
        System.out.println(address);
        System.out.println(ZIP + " " + location);
    }



}
