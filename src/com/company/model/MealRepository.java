package com.company.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MealRepository implements IRepository {

    private DBConnector dbConnector;

    public MealRepository (){
        this.dbConnector = DBConnector.getInstance();
    }

    @Override
    public ArrayList findAll() {
        ArrayList<Meal> meals = new ArrayList<>();
        ResultSet rs = dbConnector.fetchData("SELECT menu.menu_nr, menu.name, menu_gruppe.name, menu.preis " +
                "FROM `menu` " +
                "INNER JOIN menu_gruppe ON menu.menu_gruppe = menu_gruppe.id");
        try {
            while (rs.next()) {
                Meal meal = new Meal(rs.getInt("menu.menu_nr"), rs.getString("menu.name"),
                        rs.getString("menu_gruppe.name"), rs.getDouble("menu.preis"));

                ArrayList<Ingredient> ingredients = new ArrayList<>();
                ResultSet subRs = dbConnector.fetchData("SELECT zutaten.id, zutaten.name, zutaten.vegetarisch, zutaten.preis " +
                        "FROM `zutatenmix` " +
                        "INNER JOIN zutaten ON zutatenmix.zutaten_id = zutaten.id " +
                        "WHERE zutatenmix.menü_id = " + rs.getInt("menu.menu_nr"));
                try {
                    while (subRs.next()) {
                        ingredients.add(new Ingredient(subRs.getInt("zutaten.id"), subRs.getString("zutaten.name"),
                                subRs.getBoolean("zutaten.vegetarisch"), subRs.getDouble("zutaten.preis")));

                        if (!subRs.getBoolean("zutaten.vegetarisch")) {
                            meal.setVegetarian(false);
                        }
                        meal.setIngredients(ingredients);
                    }
                } catch (SQLException ex) {
                    System.out.println("couldn't find ingredients");
                    ex.printStackTrace();
                } // closeConnection?? does it break the current process?

                meals.add(meal);

            }
            return meals;
        } catch (SQLException ex) {
            System.out.println("couldn't get all meals");
            ex.printStackTrace();
        } finally {
            dbConnector.closeConnection();
        }
        return null;
    }

    @Override
    public Meal findOne(int id) {
        Meal meal = null;
        ResultSet rs = dbConnector.fetchData("SELECT menu.name, menu_gruppe.name, menu.preis " +
                "FROM `menu` " +
                "INNER JOIN menu_gruppe ON menu.menu_gruppe = menu_gruppe.id " +
                "WHERE menu_nr = " + id);
        try {
            while (rs.next()){
                meal = new Meal(id, rs.getString("menu.name"), rs.getString("menu_gruppe.name"), rs.getDouble("menu.preis"));

                ArrayList<Ingredient> ingredients = new ArrayList<>();
                ResultSet subRs = dbConnector.fetchData("SELECT zutaten.id, zutaten.name, zutaten.vegetarisch, zutaten.preis " +
                        "FROM `zutatenmix` " +
                        "INNER JOIN zutaten ON zutatenmix.zutaten_id = zutaten.id " +
                        "WHERE zutatenmix.menü_id = " + id);
                try {
                    while (subRs.next()){
                        ingredients.add(new Ingredient(subRs.getInt("zutaten.id"), subRs.getString("zutaten.name"),
                                subRs.getBoolean("zutaten.vegetarisch"), subRs.getDouble("zutaten.preis")));

                        if (!subRs.getBoolean("zutaten.vegetarisch")){
                            meal.setVegetarian(false);
                        }
                    }
                } catch (SQLException ex){
                    System.out.println("couldn't find ingredients");
                    ex.printStackTrace();
                } // closeConnection?? does it break the current process?
                meal.setIngredients(ingredients);
            }
            return meal;

        } catch (SQLException ex){
            System.out.println("couldn't get all meals");
            ex.printStackTrace();
        } finally {
            dbConnector.closeConnection();
        }
        return null;
    }

    @Override
    public boolean create(Object entity) {
        return false;
    }
    private boolean statusInProgress (Order order) {
        ResultSet rs = dbConnector.fetchData("SELECT `abgeschlossen` FROM `bestellung` WHERE `bestellnr` = " + order.getOrderNo());
        try {
            if (rs.next()){
                if (rs.getInt("abgeschlossen") == 0){
                    return true;
                }
            }
        } catch (SQLException ex){
            System.out.println("couldn't get status of order");
            ex.printStackTrace();
        }
        return false;
    }

    public void deleteOrderDetails (Order order) {
        if (statusInProgress(order)){
            for (int i = 0; i < order.getChosenMeals().size(); i++) {
                if(!dbConnector.delete("DELETE FROM `zutaten_hinzuf` WHERE `id_detail_auswahl` = " + order.getChosenMeals().get(i).getOrderDetailsID()) ||
                        !dbConnector.delete("DELETE FROM `zutaten_entfernen` WHERE `id_detail_auswahl` = " + order.getChosenMeals().get(i).getOrderDetailsID())){
                return;
                }
            }
        }
    }
}
