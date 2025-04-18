package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The StaffHomePage class provides the landing page for staff users.
 * It displays a welcome message and navigation options specific to staff.
 * These options include accessing the discussion board, initiating staff private messaging,
 * changing roles, and logging out.
 *
 * This implementation supports the following staff-related user stories:
 * <ul>
 *   <li>Staff can view discussion board questions and answers.</li>
 *   <li>Staff can read private chats between students and instructors.</li>
 *   <li>Staff can initiate private messages with instructors or other staff members.</li>
 *   <li>Staff can review their private messages.</li>
 * </ul>
 *
 * @author 
 * @version 1.0
 */
public class StaffHomePage {

    private final DatabaseHelper databaseHelper;

    /**
     * Constructs a StaffHomePage using the specified DatabaseHelper.
     *
     * @param databaseHelper the helper object for database operations.
     */
    public StaffHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Displays the staff home page.
     *
     * @param primaryStage the primary stage for the application.
     * @param user the currently logged-in staff member.
     */
    public void show(Stage primaryStage, User user) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Display greeting message
        Label staffLabel = new Label("Hello, Staff!");
        staffLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to navigate to the Staff Private Messaging interface
        Button staffMessagesButton = new Button("Staff Private Messaging");
        staffMessagesButton.setOnAction(e -> {
            new StaffPrivateMessagesPage(databaseHelper).show(primaryStage, user);
        });
        
        // Optional: Button to access the discussion board
        Button discussionBoardButton = new Button("Go to Discussion Board");
        discussionBoardButton.setOnAction(e -> {
            new DiscussionBoardPage(databaseHelper).show(primaryStage, user);
        });
        
        // Button for role change
        Button changeRoleButton = new Button("Change Role");
        changeRoleButton.setOnAction(a -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
        });
        
        // Log Out button
        Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(a -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        layout.getChildren().addAll(
            staffLabel,
            staffMessagesButton,
            discussionBoardButton,
            changeRoleButton,
            logoutButton
        );
        
        Scene staffScene = new Scene(layout, 800, 400);
        primaryStage.setScene(staffScene);
        primaryStage.setTitle("Staff Page");
    }
}
