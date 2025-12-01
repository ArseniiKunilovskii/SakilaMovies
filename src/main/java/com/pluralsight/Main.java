package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (BasicDataSource dataSource = new BasicDataSource()) {
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
            dataSource.setUsername(args[0]);
            dataSource.setPassword(args[1]);
            try (Connection connection = dataSource.getConnection();
                Scanner scanner = new Scanner(System.in)) {

                if (args.length != 2) {
                    System.out.println("Application needs two arguments to run: " +
                            "java com.pluralsight.Main <username> <password>");
                    System.exit(1);
                }

                System.out.println("Hello! You are in the SakilaMovies archive.");
                String lastname = "";
                while (true){
                    System.out.println("Please enter a last name of an actor you like:");
                    lastname = scanner.nextLine();
                    displayActorsByLastName(connection, lastname);
                    System.out.println("Please specify the first name of an actor you like:");
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void displayActorsByLastName(Connection connection, String lastname){
        try (PreparedStatement statement = connection.prepareStatement("Select first_name, last_name from actor " +
                "where last_name = ?")){
            statement.setString(1, lastname);
            try (ResultSet results = statement.executeQuery()){
                if (!results.next()){
                    System.out.println("There is no actor with the last name " + lastname);
                    return;
                }
               do {
                    String firstName = results.getString(1);
                    String lastName = results.getString(2);
                    System.out.println(firstName + " " + lastName);
                } while (results.next());
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}