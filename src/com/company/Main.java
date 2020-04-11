package com.company;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Main {
    private static DecimalFormat df = new DecimalFormat("##.##");

    public static void main(String[] args) {
        //todo: Auswahl Menü anzeigen
        printMenu();
        //todo: Menüwahl und Zutaten zufügen
        //todo: Kundendaten eingeben
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
}
