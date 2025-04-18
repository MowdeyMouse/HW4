package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * AreYouSure class represents the user interface for the are you sure page.
 * This page displays message and 2 buttons for the admin to delete a user.
 */

public class UserDeletedPage {
	private DatabaseHelper databaseHelper = new DatabaseHelper();
	public UserDeletedPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	/**
     * Displays the delete page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, User user, String delete) {
    	VBox layout = new VBox(10);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    //create deletion success label
	    Label delSuc = new Label();
	    delSuc.setText("User " + delete + " deleted");
	    
	    // create back button
	    Button backButton = new Button("Back");
	    
	    backButton.setOnAction(a -> {
	    	new AdminHomePage(databaseHelper).show(primaryStage, user);
	    });
	    

	    layout.getChildren().addAll(delSuc, backButton);
	    Scene userDeletedScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(userDeletedScene);
	    primaryStage.setTitle("User Deleted");
    }
}