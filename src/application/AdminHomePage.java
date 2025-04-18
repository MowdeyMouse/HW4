package application;

import databasePart1.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	private final DatabaseHelper databaseHelper;

    public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox(10);
    	
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display the welcome message for the admin
	    Label adminLabel = new Label("Hello, Admin!");
	    
	    adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // "Invite" button for admin to generate invitation codes
        Button inviteButton = new Button("Invite");
        inviteButton.setOnAction(a -> {
            new InvitationPage().show(databaseHelper, primaryStage, user);
        });
        
	    // Route to userListing
	    Button userListButton = new Button("List Users");
	    userListButton.setOnAction(a -> {
	    	new UserListingPage(databaseHelper).show(primaryStage, user);}
	    );
	    
	    // Route to Role change
	    Button roleChangeButton = new Button("Change User Roles");
	    roleChangeButton.setOnAction(a -> {
	    	new RoleChange(databaseHelper).show(primaryStage, user);}
	    );
	    
	    // Create delete user button
	    Button deleteUserButton = new Button("Delete User");
	    deleteUserButton.setOnAction(a -> {
	    	new DeleteUserPage(databaseHelper).show(primaryStage, user);
	    });
	    
	    // Set one time password
	    Button setOneTimePassword = new Button("Set One-Time Password");
	    setOneTimePassword.setOnAction(a -> {
	    	new SetOneTimePassword(databaseHelper).show(primaryStage, user);
	    });
	    
        // Change Role
        Button changeRoleButton = new Button("Change Role");
        changeRoleButton.setOnAction(a -> {
        	new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });
        
	    // Log out
	    Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(a -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

	    layout.getChildren().addAll(adminLabel, inviteButton, userListButton, roleChangeButton, deleteUserButton, setOneTimePassword, changeRoleButton, logoutButton);
	    Scene adminScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin Page");
    }
}