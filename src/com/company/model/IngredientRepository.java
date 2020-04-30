package com.company.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class IngredientRepository implements IRepository {
    private DBConnector dbConnector;

    public IngredientRepository() {
        this.dbConnector = DBConnector.getInstance();
    }

    @Override
    public ArrayList<Ingredient> findAll() {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ResultSet rs = dbConnector.fetchData("SELECT * FROM `zutaten` ");
        try {
            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                boolean isVegetarian = rs.getBoolean("vegetarisch");
                double singlePrice = rs.getDouble("preis");
                ingredients.add(new Ingredient(id, name, isVegetarian, singlePrice));
            }
        } catch (SQLException ex){
            System.out.println("couldn't get ingredient data");
            ex.printStackTrace();
        } finally {
            dbConnector.closeConnection();
        }
        return ingredients;
    }

    @Override
    public Ingredient findOne(int id) {
        ResultSet rs = dbConnector.fetchData("SELECT * FROM `zutaten` WHERE id = " + id);
        try {
            if (rs.next()){
                return new Ingredient(id, rs.getString("name"),
                        rs.getBoolean("vegetarisch"), rs.getDouble("preis"));
            }
        } catch (SQLException ex){
            System.out.println("couldn't find ingredient data");
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(Object entity) {
        return false;
    }
}
