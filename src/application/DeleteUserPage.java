package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * AdminPage class represents the user interface for the delete user page.
 * This page displays a text box and button for the admin to delete a user.
 */

public class DeleteUserPage {
	private DatabaseHelper databaseHelper = new DatabaseHelper();
	public DeleteUserPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	/**
     * Displays the delete page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox(10);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // label to display the delete prompt for the admin
	    Label promptLabel = new Label("Input the user to be deleted");
	    
	    promptLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // create a text box.
	    TextField tbox = new TextField();
	    tbox.setPromptText("Enter userName");
	    
	    //create deletion success label
	    Label delSuc = new Label();
	    
	    // create back button
	    Button backButton = new Button("Back");
	    
	    // create delete user button
	    Button deleteUserButton = new Button("Delete User");
	    deleteUserButton.setOnAction(a -> {
	    	//show are you sure message
	    	new AreYouSure(databaseHelper).show(primaryStage, user, tbox.getText());
	    });
	    
	    backButton.setOnAction(a -> {
	    	new AdminHomePage(databaseHelper).show(primaryStage, user);
	    });
	    

	    layout.getChildren().addAll(promptLabel, tbox, deleteUserButton, backButton, delSuc);
	    Scene deleteUserScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(deleteUserScene);
	    primaryStage.setTitle("Delte User");
    }
}