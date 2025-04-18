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

public class ResetPasswordPage {
	private DatabaseHelper databaseHelper = new DatabaseHelper();
	public ResetPasswordPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	/**
     * Displays the delete page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, User user, String resetee) {
    	VBox layout = new VBox(10);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the prompt for the user
	    Label promptLabel = new Label("It looks like you've logged in using a one-time password, please enter a new password:");
	    
	    promptLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // create password field
	    PasswordField passwordField = new PasswordField();
	    
	    // create password error label
	    Label message = new Label();
	    Label passwordErrLabel = new Label();
	    passwordErrLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
	    
	    // create set password button
	    Button setPass = new Button("Set Password");
	    setPass.setOnAction(a -> {
	    	// get the string entered for the password
	    	String password = passwordField.getText();
	    	String passwordErr = PasswordEvaluator.evaluatePassword(password);
	    	try {
	    		if(passwordErr == "") {
	    			databaseHelper.setPassword(resetee, password, 0);
	    			message.setText("Password set");
	    			passwordErrLabel.setText("");
	    		}
	    		else {
	    			passwordErrLabel.setText(passwordErr);
	    		}
			} catch (SQLException e) {
				e.printStackTrace();
				passwordErrLabel.setText("Error trying to set password");
			}
	    });
	    
	    // Log out
	    Button backButton = new Button("Back to Login");
	    backButton.setOnAction(e -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

	    layout.getChildren().addAll(promptLabel, passwordField, setPass, backButton, message, passwordErrLabel);
	    Scene resetScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(resetScene);
	    primaryStage.setTitle("Reset Password");
    }
}