package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;

import databasePart1.*;

/**
 * This page displays a simple welcome message for the instructor.
 */
public class GrantRequestsPage {

    private final DatabaseHelper databaseHelper;
    private String selectedItem = null;

    public GrantRequestsPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) throws SQLException {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to guide the instructor
        Label instructorLabel = new Label("Select a user to grant the reviewer role to");
        instructorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Username list
        ListView<String> userListView = new ListView<>();
        ObservableList<String> userItems = FXCollections.observableArrayList(databaseHelper.getReviewerRequests());
        userListView.setItems(userItems);

        // Contributions list
        ListView<String> contributionListView = new ListView<>();
        ObservableList<String> contributionItems = FXCollections.observableArrayList();
        contributionListView.setItems(contributionItems);

        // Listener: when user is selected, update selectedItem and show contributions
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedItem = newVal;
            System.out.println("Selected user: " + selectedItem);
            try {
                ArrayList<String> contributions = databaseHelper.getUserContributions(selectedItem);
                contributionItems.setAll(contributions);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Grant reviewer role button
        Button grantRequestsButton = new Button("Grant Reviewer Role");
        grantRequestsButton.setOnAction(e -> {
            if (selectedItem != null) {
                databaseHelper.setUserRole(selectedItem, "reviewer", true);
                System.out.println("Granted reviewer role to: " + selectedItem);
            } else {
                System.out.println("No user selected!");
            }
        });

        // Log out button
        Button backButton = new Button("Log Out");
        backButton.setOnAction(a -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

        // Add everything to layout
        layout.getChildren().addAll(
            instructorLabel,
            userListView,
            new Label("Selected User's Contributions:"),
            contributionListView,
            grantRequestsButton,
            backButton
        );

        Scene instructorScene = new Scene(layout, 800, 500);
        primaryStage.setScene(instructorScene);
        primaryStage.setTitle("Instructor Page");
    }
}