package com.company.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderRepository implements IRepository {

    private DBConnector dbConnector;

    public OrderRepository() {
        this.dbConnector = DBConnector.getInstance();
    }

    @Override
    public List findAll() {
        return null;
    }

    @Override
    public Object findOne(int id) {
        return null;
    }

    @Override
    public boolean create(Object entity) {
        Order order = (Order) entity;
        if (dbConnector.insert("INSERT INTO `bestellung`(`abgeschlossen`) VALUES (" + order.getOrderStatus() + ")")) {
            //get orderNo as last AUTO_INCREMENT but since connection was closed LAST_INSERT_ID() is not possible
            //for now MAX(bestellnr) works but not perfect.
            ResultSet rs = dbConnector.fetchData("SELECT MAX(bestellnr) FROM bestellung");
            try {
                if (rs.next()) {
                    order.setOrderNo(rs.getInt("MAX(bestellnr)"));
                    return true;
                }
            } catch (SQLException ex) {
                System.out.println("couldn't get the Order No.");
                ex.printStackTrace();
            } finally {
                dbConnector.closeConnection();
            }
        }
        return false;
    }

    public boolean updateOrderDetails(Order order) {
        //List Chosen Meals (insert in 'menüauswahl': orderno, anzahl, menünummer - get 'detail_auswahl_nr')
        for (int i = 0; i < order.getChosenMeals().size(); i++) {
            if (dbConnector.insert("INSERT INTO `menu_auswahl`(`bestell_nr`, `anzahl`, `menu_nr`) " +
                    "VALUES (" + order.getOrderNo() + ", " + order.getChosenMeals().get(i).getAmount() +
                    ", " + order.getChosenMeals().get(i).getId() + ")")) {
                // get detail_auswahl_id
                ResultSet rs = dbConnector.fetchData("SELECT MAX(id_detail_auswahl) " +
                        "FROM menu_auswahl WHERE bestell_nr = " + order.getOrderNo());
                int orderDetailsID = 0;
                try {
                    if (rs.next()) {
                        orderDetailsID = rs.getInt("MAX(id_detail_auswahl)");
                        order.getChosenMeals().get(i).setOrderDetailsID(orderDetailsID);
                    }
                } catch (SQLException ex) {
                    System.out.println("couldn't get id for order details");
                    ex.printStackTrace();
                } finally {
                    dbConnector.closeConnection();
                }

                //List addIngredient: check if contains sth. before inserting in 'zutatenzufügen': id_detailauswahl, zutaten_id
                if (order.getChosenMeals().get(i).getAddIngredients().size() > 0) {
                    for (int j = 0; j < order.getChosenMeals().get(i).getAddIngredients().size(); j++) {
                        if (!dbConnector.insert("INSERT INTO `zutaten_hinzuf`(`id_detail_auswahl`, `zutaten_id`) " +
                                "VALUES (" + orderDetailsID + ", " +
                                order.getChosenMeals().get(i).getAddIngredients().get(j).getId() + ")")) {
                            return false;
                        }
                    }
                }
                //List deleteIngred: check if contains sth. before inserting in 'zutaten entfernen': id detailaswahl, zutaten_id)
                if (order.getChosenMeals().get(i).getTakeOffIngredients().size() > 0) {
                    for (int j = 0; j < order.getChosenMeals().get(i).getTakeOffIngredients().size(); j++) {
                        if (!dbConnector.insert("INSERT INTO `zutaten_hinzuf`(`id_detail_auswahl`, `zutaten_id`) " +
                                "VALUES (" + orderDetailsID + ", " +
                                order.getChosenMeals().get(i).getTakeOffIngredients().get(j).getId() + ")")) {
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public void calculateDeliveryZone (Order order, int locationId){
        ResultSet rs = dbConnector.fetchData("SELECT `id`, `lieferpreis` FROM `lieferzone` " +
                "WHERE distance_min <= (SELECT belieferte_ortschaften.distance FROM belieferte_ortschaften WHERE id = " + locationId + ") " +
                "AND distance_max > (SELECT belieferte_ortschaften.distance FROM belieferte_ortschaften WHERE id = " + locationId + ") ");
        try {
            if (rs.next()){
                order.setDeliveryZone(rs.getInt("id"));
                order.setDeliveryFee(rs.getDouble("lieferpreis"));
            }
        } catch (SQLException ex){
            System.out.println("couldn't get the delivery data");
            ex.printStackTrace();
        } finally {
            dbConnector.closeConnection();
        }
    }

    public void updateOrderStatus (int id){
        dbConnector.update("UPDATE `bestellung` SET `abgeschlossen`= 1 WHERE `bestellnr` = " + id);
    }

    public boolean checkCurrentOrder (Order order) {
        if (order.getChosenMeals().size() > 0) {
            return true;
        } else {
            System.out.println("choose a meal first");
            return false;
        }
    }

    public void deleteOrder (Order order) {
        if (statusInProgress(order)){
            dbConnector.delete("DELETE FROM `bestellung` WHERE `bestellnr`= " + order.getOrderNo());
        }
    }

    public void deleteChosenMeals (Order order) {
        if (statusInProgress(order)){
            dbConnector.delete("DELETE FROM `menu_auswahl` WHERE `bestell_nr` = " + order.getOrderNo());
        }
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
}
