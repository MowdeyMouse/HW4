package application;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * AreYouSure class represents the user interface for the are you sure page.
 * This page displays message and 2 buttons for the admin to delete a user.
 */

public class SetOneTimePassword {
	private DatabaseHelper databaseHelper = new DatabaseHelper();
	public SetOneTimePassword(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	/**
     * Displays the delete page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox(10);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the are you sure? prompt for the admin
	    Label promptLabel = new Label("Please provide the username and one-time password");
	    
	    promptLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // create userName and password fields
	    TextField userNameField = new TextField();
	    TextField passwordField = new TextField();
	    
	    // create set success label
	    Label errorLabel = new Label();
	    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
	    
	    // create back button
	    Button backButton = new Button("Back");
	    
	    // create yes button
	    Button setPass = new Button("Set Password");
	    setPass.setOnAction(a -> {
	    	// get the strings entered for the userName and password
	    	String userName = userNameField.getText();
	    	String password = passwordField.getText();
	    	String passwordErr = PasswordEvaluator.evaluatePassword(password);
	    	try {
	    		if(!databaseHelper.doesUserExist(userName)) {
	    			errorLabel.setText("User does not exist");
	    		}
	    		else if(passwordErr == "") {
	    			databaseHelper.setPassword(userName, password, 1);
	    			errorLabel.setText("Password set");
	    		}
	    		else {
	    			errorLabel.setText(passwordErr);
	    		}
			} catch (SQLException e) {
				e.printStackTrace();
				errorLabel.setText("Error trying to set password");
			}
	    });
	    
	    backButton.setOnAction(a -> {
	    	new AdminHomePage(databaseHelper).show(primaryStage, user);
	    });
	    

	    layout.getChildren().addAll(promptLabel, userNameField, passwordField, setPass, backButton, errorLabel);
	    Scene setPasswordScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(setPasswordScene);
	    primaryStage.setTitle("Set Password");
    }
}