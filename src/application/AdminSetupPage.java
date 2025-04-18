package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
	
	private String userName, email, name, password;
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Admin mail");
        emailField.setMaxWidth(250);
        
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Admin name");
        nameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);

        Button setupButton = new Button("Setup");
        
        // create error labels
        Label userErrLabel = new Label();
    	Label passErrLabel = new Label();
    	Label emailErrLabel = new Label();
    	userErrLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	passErrLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	emailErrLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	
        setupButton.setOnAction(a -> {
        	// Retrieve user input
        	userName = userNameField.getText();
        	email = emailField.getText();
        	name = nameField.getText();
            password = passwordField.getText();
            
            String userNameErr = UserNameRecognizer.checkForValidUserName(userName);
            String passwordErr = PasswordEvaluator.evaluatePassword(password);
            String emailErr = EmailEvaluator.evaluateEmail(email);
            
            userErrLabel.setText(userNameErr);
        	passErrLabel.setText(passwordErr);
        	emailErrLabel.setText(emailErr);
        	
            if(userNameErr.equals("") && passwordErr.equals("") && emailErr.equals("")) {       
            	try {
            		// Create a new User object with "admin" as the initial role
            		User user=new User(userName, email, name, password, "admin");
            		// Insert this user into the database
            		databaseHelper.register(user);

                    // Gives Admin all roles
                    databaseHelper.setUserRole(userName, "admin", true);
                    databaseHelper.setUserRole(userName, "student", true);
                    databaseHelper.setUserRole(userName, "instructor", true);
                    databaseHelper.setUserRole(userName, "staff", true);
                    databaseHelper.setUserRole(userName, "reviewer", true);
                    
                    System.out.println("Administrator setup completed. Please log in with your new admin credentials.");
                
            		// Make Admin log in again
                    new UserLoginPage(databaseHelper).show(primaryStage);
            	} catch (SQLException e) {
            		System.err.println("Database error: " + e.getMessage());
            		e.printStackTrace();
            	}
            }
        });

        VBox layout = new VBox(10, userNameField, emailField, nameField, passwordField, setupButton, userErrLabel, emailErrLabel, passErrLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}