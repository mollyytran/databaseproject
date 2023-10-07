//Molly Tran
//CS 56 Section 1733
//Assignment- Database Project 

package com.example.databaseproject;
import javafx.application.Application;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;


public class DatabaseProject extends Application {

    private static Statement statement;
    private static Connection connection;

    @Override
    public void start(Stage stage) {
        initializeDB();

        // Create text fields
        TextField txtID = new TextField();
        TextField txtLastName = new TextField();
        TextField txtFirstName = new TextField();
        TextField txtMiddle = new TextField();
        TextField txtAddress = new TextField();
        TextField txtCity = new TextField();
        TextField txtState = new TextField();
        TextField txtTelephone = new TextField();
        TextField txtEmail = new TextField();

        txtState.setPrefWidth(30);
        txtMiddle.setPrefWidth(40);

        //Create buttons
        Button btnView = new Button("View");
        Button btnInsert = new Button("Insert");
        Button btnUpdate = new Button("Update");
        Button btnClear = new Button("Clear");

        // Allow user to view the data based on ID inputted
        btnView.setOnAction(e -> {
            try {
                String id = txtID.getText();
                String selectQuery = "SELECT * FROM staff WHERE id = '" + id + "'";
                ResultSet resultSet = statement.executeQuery(selectQuery);

                if (resultSet.next()) {
                    txtLastName.setText(resultSet.getString("lastName"));
                    txtFirstName.setText(resultSet.getString("firstName"));
                    txtMiddle.setText(resultSet.getString("mi"));
                    txtAddress.setText(resultSet.getString("address"));
                    txtCity.setText(resultSet.getString("city"));
                    txtState.setText(resultSet.getString("state"));
                    txtTelephone.setText(resultSet.getString("telephone"));
                    txtEmail.setText(resultSet.getString("email"));
                    System.out.println("Record found and displayed");
                } else {

                    txtLastName.clear();
                    txtFirstName.clear();
                    txtMiddle.clear();
                    txtAddress.clear();
                    txtCity.clear();
                    txtState.clear();
                    txtTelephone.clear();
                    txtEmail.clear();
                    System.out.println("Record not found");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        //Insert values inputted by user into the database
        btnInsert.setOnAction(e -> {
            try {
                String insertStatement = "INSERT INTO staff (id, lastName, firstName, mi, address, city, state, telephone, email) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement preparedStatement = statement.getConnection().prepareStatement(insertStatement);
                preparedStatement.setString(1, txtID.getText());
                preparedStatement.setString(2, txtLastName.getText());
                preparedStatement.setString(3, txtFirstName.getText());
                preparedStatement.setString(4, txtMiddle.getText());
                preparedStatement.setString(5, txtAddress.getText());
                preparedStatement.setString(6, txtCity.getText());
                preparedStatement.setString(7, txtState.getText());
                preparedStatement.setString(8, txtTelephone.getText());
                preparedStatement.setString(9, txtEmail.getText());

                preparedStatement.executeUpdate();
                System.out.println("Record inserted successfully");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });


        // Set update button to retrieve data with ID entered, allows user to update any text fields in the record
        btnUpdate.setOnAction(e -> {
            try {

                String id = txtID.getText();
                String updateQuery = "UPDATE staff SET lastName = ?, firstName = ?, mi = ?, address = ?, city = ?, state = ?, telephone = ?, email = ? WHERE id = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setString(1, txtLastName.getText());
                preparedStatement.setString(2, txtFirstName.getText());
                preparedStatement.setString(3, txtMiddle.getText());
                preparedStatement.setString(4, txtAddress.getText());
                preparedStatement.setString(5, txtCity.getText());
                preparedStatement.setString(6, txtState.getText());
                preparedStatement.setString(7, txtTelephone.getText());
                preparedStatement.setString(8, txtEmail.getText());
                preparedStatement.setString(9, id);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Record updated successfully");
                } else {
                    System.out.println("No record found with ID: " + id);
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Set clear button to clear all text fields
        btnClear.setOnAction(e -> {
            txtID.clear();
            txtLastName.clear();
            txtFirstName.clear();
            txtMiddle.clear();
            txtAddress.clear();
            txtCity.clear();
            txtState.clear();
            txtTelephone.clear();
            txtEmail.clear();
        });

        // Create container to hold buttons
        HBox buttonBox = new HBox(btnView, btnInsert, btnUpdate, btnClear);
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        // Create containers to hold the labels and text fields
        HBox idBox = new HBox(new Label("ID"), txtID);
        idBox.setSpacing(10);
        HBox nameBox = new HBox(new Label("Last Name"), txtLastName,new Label("First Name"), txtFirstName,new Label("MI"), txtMiddle);
        nameBox.setSpacing(10);
        HBox addressBox = new HBox(new Label("Address"),txtAddress);
        addressBox.setSpacing(10);
        HBox cityBox = new HBox(new Label("City"),txtCity, new Label("State"),txtState);
        cityBox.setSpacing(10);
        HBox phoneBox =new HBox(new Label("Telephone"),txtTelephone, new Label("Email"), txtEmail);
        phoneBox.setSpacing(10);

        // Add all containers to the flow pane
        FlowPane flowPane = new FlowPane(idBox,nameBox,addressBox,cityBox,phoneBox);
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(10));

        //Create a border pane and add the label, flow pane (labels and text field), and the buttons to it
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        pane.setTop(new Label("Staff Information"));
        pane.setCenter(flowPane);
        pane.setBottom(buttonBox);

        // Create a scene and place it in the stage
        Scene scene = new Scene(pane, 550, 225);
        stage.setTitle("Exercise37_1!");
        stage.setScene(scene);
        stage.show();

    }

    // Connect to the database
    private static void initializeDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql?serverTimezone=UTC", "root", "adminadmin");
            System.out.println("Database connected");
            statement = connection.createStatement();
            statement.execute("use staff");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // Close the database
    @Override
    public void stop() {
        try {
            connection.close();
            System.out.println("Database connection closed.");
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}