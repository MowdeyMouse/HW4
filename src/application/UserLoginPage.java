package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import java.util.ArrayList;


import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Create the initial login form
    	VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button loginButton = new Button("Login");
        
        loginButton.setOnAction(a -> {
        	// Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();
            
            try {
            	// Create a User object (role is initially blank; we'll set it after we determine it)
            	User user=new User(userName, "", "", password, "");
            	
            	// Get roles for the user from the database
            	ArrayList<String> roles = databaseHelper.getUserRoles(userName);
            	
            	// No roles
            	if(!databaseHelper.doesUserExist(userName)) {
            		// User account not found
                    errorLabel.setText("User account does not exist.");
            	} else {
                	// First verify password/credentials are correct
                	boolean credentialsValid = databaseHelper.login(user);
                	if (!credentialsValid) {
                		errorLabel.setText("Error logging in (invalid credentials).");
                		return;
                	}
                	
                	user = databaseHelper.getUser(userName);
                	if(roles.isEmpty()) {
                		// User account no roles assigned
                        errorLabel.setText("User does not have roles assigned.");
                    } else if(databaseHelper.isOneTime(userName)) {
        				new ResetPasswordPage(databaseHelper).show(primaryStage, user, userName);
        			} else if(roles.size() == 1) {
        				databaseHelper.switchRole(user.getUserName(), roles.get(0));
                        user.setRole(roles.get(0));
                        HomePageNavigator.navigateToRoleHome(databaseHelper, primaryStage, user, roles.get(0));
        			} else {
        				new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
        			}
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            } 
        });
        
        // Back button
	    Button backToLoginButton = new Button("Back");
	    backToLoginButton.setOnAction(a -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

        layout.getChildren().addAll(userNameField, passwordField, loginButton, backToLoginButton, errorLabel);

        // Create and set the scene
        Scene loginScene = new Scene(layout, 800, 400);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}