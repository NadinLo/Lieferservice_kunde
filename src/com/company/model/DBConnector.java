package com.company.model;

import java.sql.*;

public class DBConnector {
    private Connection connection = null;
    private Statement statement = null;
    private static final String url = "jdbc:mysql://localhost:3306/lieferservice_gastro?user=root";

    private static DBConnector instance;

    public static DBConnector getInstance(){
        if(instance == null){
            instance = new DBConnector();
        }
        return instance;
    }

    private DBConnector(){

    }

    private void buildConnection() {
        try {
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
        } catch (SQLException e) {
            System.out.println("could not build connection");
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println("could not close connection");
            e.printStackTrace();
        }
    }

    public ResultSet fetchData(String sql) {
        buildConnection();
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("could not fetch data");
            e.printStackTrace();
            closeConnection();
        }
        return null;
    }

    public boolean delete(String sql) {
        buildConnection();
        try {
            int result = statement.executeUpdate(sql);
            if (result == 0) {
                System.out.println("no matching entry found");
                return false;
            } else {
                System.out.println("delete successful, update your data");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("could not delete data");
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }

    public void update(String sql) {
        buildConnection();
        try{
            int result = statement.executeUpdate(sql);
            if (result == 0) {
                System.out.println("no matching entry");
            } else {
                System.out.println("update successful");
            }
        } catch (SQLException e){
            System.out.println("could not update data");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public boolean insert(String sql) {
        buildConnection();
        try{
            int result = statement.executeUpdate(sql);
            if (result == 0) {
                System.out.println("no matching entry");
                return false;
            } else {
                System.out.println("insert was successful");
                return true;
            }
        } catch (SQLException e){
            System.out.println("could not insert data");
            e.printStackTrace();
            return false;
        } finally {
            closeConnection();
        }
    }
}
