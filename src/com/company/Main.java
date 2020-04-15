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
            //todo: finish programm
        }
        while (decision.equalsIgnoreCase("Y")) {
            //todo: Auswahl Menü anzeigen: funktioniert noch nicht.
            printMenu();

//Choose menu:
            int menuNo = 0;
            int id = 0;
            decision = null;
            System.out.println("choose a menu. Enter the number");
            menuNo = scannerForInt.nextInt();
            id = chooseMenu(orderNo, menuNo);
            if (id == 0) {
                System.out.println("Something went wrong. Enter again a menu number");
            }
//Change ingredients:
            System.out.println("Do you want to change ingredients? (Y/N)");
            decision = scannerForString.next();
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
            decision = scannerForString.next();
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

        // todo: overview order in total
        overview (orderNo);
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

    private static void enterCustomerData(int orderNo, String name, String address, String location){
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt = null;
            String query = "SELECT id, name FROM belieferte_ortschaften";
            stmt = conn.createStatement();
            int locationID = 0;
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                locationID = rs.getInt("id");
                String locationName = rs.getString("name");
                if (!locationName.equalsIgnoreCase(location)){
                    locationID = 0;
                }
                else {
                    break;
                }
            }
            if (locationID != 0){
                String command = "INSERT INTO `kunde`(`bestellnr`, `name`, `straße_hnr`, `ortschaft`) " +
                        "VALUES (" + orderNo + ", '" + name + "', '" + address + "', " + locationID + ")";
                stmt = conn.createStatement();
                stmt.executeUpdate(command);
                calculateDeliveryArea(orderNo);
            }
            else {
                System.out.println("We don't deliver to this area. Sorry.");
            }

        } catch (SQLException ex){
            throw new Error("Problem", ex);
        }
    }
    
    private static void calculateDeliveryArea (int orderNo){
        Connection conn = null;
        double deliveryFee = 0;
        int deliveryArea = 0;
        try {
            String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt = null;
            String query = "SELECT lieferzone.id, lieferzone.lieferpreis " +
                    "FROM lieferzone " +
                    "WHERE lieferzone.id = (SELECT lieferzone.id " +
                    "FROM lieferzone " +
                    "WHERE lieferzone.distance_min < " +
                    "(SELECT belieferte_ortschaften.distance " +
                    "FROM belieferte_ortschaften " +
                    "INNER JOIN kunde ON kunde.ortschaft = belieferte_ortschaften.id WHERE kunde.bestellnr = " + orderNo + ") " +
                    "AND lieferzone.distance_max > " +
                    "(SELECT belieferte_ortschaften.distance " +
                    "FROM belieferte_ortschaften " +
                    "INNER JOIN kunde ON kunde.ortschaft = belieferte_ortschaften.id WHERE kunde.bestellnr = " + orderNo + "))";
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                deliveryArea = rs.getInt("lieferzone.id");
                deliveryFee = rs.getDouble("lieferzone.lieferpreis");
            }
            System.out.println("Area " + deliveryArea + "\nPrice for delivery: " + df.format(deliveryFee) + "€");



        } catch (SQLException ex){
            throw new Error("Problem", ex);
        }
    }

    private static void overview (int orderNo){
        System.out.println("YOUR ORDER\t\t\t" + orderNo);
        Connection conn = null;
        try {
            String url= "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";
            conn = DriverManager.getConnection(url);
            Statement stmt = null;
            //kundendaten
            String query = "SELECT kunde.name AS 'Kundenname', " +
                    "kunde.straße_hnr as 'Straße Hausnummer', " +
                    "belieferte_ortschaften.plz as 'PLZ', " +
                    "belieferte_ortschaften.name AS 'Ort' " +
                    "FROM `kunde` " +
                    "INNER JOIN belieferte_ortschaften ON kunde.ortschaft = belieferte_ortschaften.id " +
                    "WHERE `bestellnr` = " + orderNo;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                String customerName = rs.getString("Kundenname");
                String address = rs.getString("Straße Hausnummer");
                String ZIP = "" + rs.getInt("PLZ");
                String location = rs.getString("Ort");
            }
        } catch (SQLException ex){
            throw new Error("Problem", ex);
        }
        //bestellung (menünummer, name, extra Zutaten, Menge, Preis Menü)
        //SELECT auswahl.bestell_nr AS 'Bestellnummer', auswahl.menu_nr AS 'Menünummer', menu.name as 'Menü', (SELECT menu_gruppe.name From menu INNER JOIN menu_gruppe ON menu.menu_gruppe = menu_gruppe.id WHERE menu.menu_gruppe = (SELECT menu.menu_gruppe FROM menu_auswahl auswahl INNER JOIN menu ON auswahl.menu_nr = menu.menu_nr WHERE auswahl.bestell_nr = 54)) AS 'Gruppe', (SELECT zutaten.name FROM zutaten_hinzuf INNER JOIN zutaten ON zutaten_hinzuf.zutaten_id = zutaten.id WHERE zutaten_hinzuf.id_detail_auswahl = (SELECT auswahl.id_detail_auswahl FROM menu_auswahl auswahl INNER JOIN menu ON auswahl.menu_nr = menu.menu_nr WHERE auswahl.bestell_nr = 54)) AS 'extra Zutaten', menu.preis AS 'Preis', auswahl.anzahl AS 'Menge' FROM menu_auswahl auswahl INNER JOIN menu ON auswahl.menu_nr = menu.menu_nr WHERE auswahl.bestell_nr = 54
        //Lieferzone, Lieferpreis
        //gesamtpreis
    }


}
