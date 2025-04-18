package application;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import databasePart1.*;

/**
 * This page displays a simple welcome message for the user.
 * Depending on the user's role, additional options (e.g., staff messaging) are provided.
 */
public class UserHomePage {

    private final DatabaseHelper databaseHelper;

    public UserHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Label to display welcome message
        Label userLabel = new Label("Hello, Student!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Discussion board button
        Button discussionButton = new Button("Discussion Board");
        discussionButton.setOnAction(a -> {
            new DiscussionBoardPage(databaseHelper).show(primaryStage, user);
        });
        
        // Reviewer request button
        Button requestButton = new Button("Request Reviewer Role");
        requestButton.setOnAction(a -> {
            try {
                databaseHelper.addReviewerRequest(user.getUserName());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        
        // Change Role button
        Button changeRoleButton = new Button("Change Role");
        changeRoleButton.setOnAction(a -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });
        
        // Conditional button for Staff Private Messaging (if user is staff)
        if (user.getRole().equalsIgnoreCase("staff")) {
            Button staffMessagesButton = new Button("Staff Private Messaging");
            staffMessagesButton.setOnAction(e -> {
                new StaffPrivateMessagesPage(databaseHelper).show(primaryStage, user);
            });
            layout.getChildren().add(staffMessagesButton);
        }
        
        // Log Out button
        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(a -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        layout.getChildren().addAll(userLabel, discussionButton, requestButton, changeRoleButton, logoutButton);
        Scene userScene = new Scene(layout, 800, 400);
        
        primaryStage.setScene(userScene);
        primaryStage.setTitle("Student Page");
    }
}
