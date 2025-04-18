package application;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.*;

/**
 * This page displays a simple welcome message for the reviewer.
 */

public class ReviewerHomePage {

    private final DatabaseHelper databaseHelper;

    public ReviewerHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, User user) {
    	VBox layout = new VBox(10);
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello Reviewer
	    Label reviewerLabel = new Label("Hello, Reviewer!");
	    reviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Discussion board
	    Button discussionButton = new Button("Discussion Board");
	    discussionButton.setOnAction(a -> {
	        // OPEN THE DISCUSSION BOARD *WITHOUT* THE PRIVATE MESSAGES TAB:
	        new DiscussionBoardPage(databaseHelper, false).show(primaryStage, user);
	    });

        // Private messages
        Button privateMessagesButton = new Button("Private Messages");
        privateMessagesButton.setOnAction(a -> {
            // OPEN A DEDICATED PRIVATE MESSAGING PAGE:
            new ReviewerPrivateMessagesPage(databaseHelper).show(primaryStage, user);
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

	    layout.getChildren().addAll(
	        reviewerLabel, 
	        discussionButton, 
	        privateMessagesButton,
	        changeRoleButton, 
	        logoutButton
	    );
	    
	    Scene reviewerScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(reviewerScene);
	    primaryStage.setTitle("Reviewer Page");
    }
}
