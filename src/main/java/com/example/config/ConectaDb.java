package com.example.config;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectaDb {

     private static final String URL = "jdbc:postgresql://localhost:5432/srsdb";
     private static final String USER = "srs_app";
     private static final String PASSWORD = "srs_local_password";

     public static Connection getConnection() throws SQLException {

         try {

             Class.forName("org.postgresql.Driver");

         } catch (ClassNotFoundException e) {

             throw new SQLException("PostgreSQL JDBC driver não encontrado.", e);

         }

        return  DriverManager.getConnection(URL, USER, PASSWORD);

     }




}
