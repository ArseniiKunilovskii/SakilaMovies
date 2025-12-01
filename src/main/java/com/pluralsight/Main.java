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
            try (Scanner scanner = new Scanner(System.in)) {

                if (args.length != 2) {
                    System.out.println("Application needs two arguments to run: " +
                            "java com.pluralsight.Main <username> <password>");
                    System.exit(1);
                }

                System.out.println("Hello! You are in the SakilaMovies archive.");
                String lastname = "";
                String firstname = "";
                while (true) {
                    System.out.println("Please enter a last name of an actor you like:");
                    lastname = scanner.nextLine();
                    displayActorsByLastName(dataSource, lastname);
                    System.out.println("Please specify the first name of an actor you like:");
                    firstname = scanner.nextLine();
                    displayMoviesByFullName(dataSource, firstname, lastname);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayActorsByLastName(BasicDataSource dataSource, String lastname) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     Select first_name, last_name from actor
                     where last_name = ?""")) {
            statement.setString(1, lastname);
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    System.out.println("There is no actor with the last name " + lastname);
                    return;
                }
                String firstName;
                String lastName;
                do {
                    firstName = results.getString(1);
                    lastName = results.getString(2);
                    System.out.println(firstName + " " + lastName);
                } while (results.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayMoviesByFullName(BasicDataSource dataSource, String firstName, String lastName) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(""" 
                     SELECT f.title FROM sakila.film f
                     join film_actor fa on f.film_id = fa.film_id
                     join actor a on fa.actor_id = a.actor_id
                     where first_name = ? AND last_name = ?;""")) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next()) {
                    System.out.println("There is no movies with this actor");
                    return;
                }
                String title;
                System.out.println("Here is the list of movies with " + firstName + " " + lastName+ "-----------------");
                do {
                    title = results.getString(1);
                    System.out.println(title);
                } while (results.next());
                System.out.println("End of the list--------------------------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
