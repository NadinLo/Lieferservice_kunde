package com.company.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserRepository implements IRepository {
    private DBConnector dbConnector;

    public UserRepository() {
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
    public boolean create (Object entity) {
        User user = (User) entity;
        return dbConnector.insert("INSERT INTO `kunde`(`bestellnr`, `name`, `straße_hnr`, `ortschaft`) " +
                "VALUES (" + user.getOrderNo() + ", '" + user.getName() + "', '" + user.getAddress() + "', " + user.getLocationId() + ")");
    }

    public boolean isLocationInsideDeliveryArea (User user){
        ResultSet rs = dbConnector.fetchData("SELECT * FROM `belieferte_ortschaften` " +
                "WHERE name LIKE '" + user.getLocation() + "'");
        try {
            if (rs.next()) {
                user.setLocationId(rs.getInt("id"));
                user.setMinutesToDeliver(rs.getTime("lieferzeit"));
                return true;
            } else {
                System.out.println("Sorry, we do not deliver to this location");
                return false;
            }
        } catch (SQLException ex){
            System.out.println("couldn't get data of location");
            ex.printStackTrace();
        } finally {
            dbConnector.closeConnection();
        }
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

    public void deleteCustomerData (Order order) {
        if (statusInProgress(order)){
            dbConnector.delete("DELETE FROM `kunde` WHERE `bestellnr` = " + order.getOrderNo());
        }
    }
}
