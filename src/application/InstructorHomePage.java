package application;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * This page displays a simple welcome message for the instructor.
 */

public class InstructorHomePage {

    private final DatabaseHelper databaseHelper;

    public InstructorHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello Instructor
	    Label instructorLabel = new Label("Hello, Instructor!");
	    instructorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
        // grant users reviewer role
        Button grantRequestsButton = new Button("Grant Reviewer Role Requests");
        grantRequestsButton.setOnAction(a -> {
        	try {
				new GrantRequestsPage(databaseHelper).show(primaryStage, user);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
        
        // Change Role
        Button changeRoleButton = new Button("Change Role");
        changeRoleButton.setOnAction(a -> {
        	new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });
        
	    // Log out
	    Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(a -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

	    layout.getChildren().addAll(instructorLabel, grantRequestsButton, changeRoleButton, logoutButton);
	    Scene instructorScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(instructorScene);
	    primaryStage.setTitle("Instructor Page");
    	
    }
}