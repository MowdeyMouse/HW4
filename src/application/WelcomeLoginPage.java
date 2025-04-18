package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show( Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    Label welcomeLabel = new Label("Welcome, " + user.getUserName() + "!!");
	    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    Label selectRoleLabel = new Label("Select a role to log in as:");
	    
	    // Buttons for each role
        ArrayList<String> roles = databaseHelper.getUserRoles(user.getUserName());
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(roles);
        
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Button confirmRoleButton = new Button("Confirm");
        confirmRoleButton.setOnAction(e -> {
            String selectedRole = roleComboBox.getValue();
            if (selectedRole == null) {
                errorLabel.setText("Please select a role.");
                return;
            }
           
            // Switch the user's role in the database, then set it in the User object
            databaseHelper.switchRole(user.getUserName(), selectedRole);
            user.setRole(selectedRole);
           
            // Now navigate to the appropriate home page based on that role
            HomePageNavigator.navigateToRoleHome(databaseHelper, primaryStage, user, selectedRole);
        });
        
        if(roles.size() == 0) {
        	errorLabel.setText("Error: User has no roles.");
        }
	    
	    // Log out
	    Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(a -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
	    layout.getChildren().addAll(welcomeLabel, selectRoleLabel, roleComboBox, confirmRoleButton, logoutButton, errorLabel);
	    
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}