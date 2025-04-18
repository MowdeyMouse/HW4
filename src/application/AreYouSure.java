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

public class AreYouSure {
	private DatabaseHelper databaseHelper = new DatabaseHelper();
	public AreYouSure(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	/**
     * Displays the delete page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, User user, String delete) {
    	VBox layout = new VBox(10);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the are you sure? prompt for the admin
	    Label promptLabel = new Label("Are you sure you want to delete " + delete + "'s account?");
	    
	    promptLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    
	    //create deletion success label
	    Label errorLabel = new Label();
	    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
	    
	    // create yes button
	    Button yes = new Button("Yes");
	    yes.setOnAction(a -> {
	    	if(user.getUserName().equals(delete)) {
	    		errorLabel.setText("Cannot delete your own account");
	    	}
	    	else if(databaseHelper.doesUserExist(delete)) {
	    		try {
	    			databaseHelper.deleteUser(delete);
	    			new UserDeletedPage(databaseHelper).show(primaryStage, user, delete);
	    		} catch (SQLException e) {
	    			e.printStackTrace();
	    			errorLabel.setText("Error deleting user");
	    		}
	    	}
	    	else {
	    		errorLabel.setText("User does not exist");
	    	}
	    });
	    
	    // create no button
	    Button no = new Button("No");
	    no.setOnAction(a -> {
	    	new DeleteUserPage(databaseHelper).show(primaryStage, user);
	    });
	    

	    layout.getChildren().addAll(promptLabel, yes, no, errorLabel);
	    Scene deleteConfirmationScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(deleteConfirmationScene);
	    primaryStage.setTitle("Deletion Confirmation");
    }
}