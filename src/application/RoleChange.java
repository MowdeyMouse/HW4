package application;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;

public class RoleChange {
	/**
	 * Displays the Role change page in the provided primary stage.
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	private final DatabaseHelper databaseHelper;

	public RoleChange(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {
		// Label to display the instructions of the page
		Label pageLabel = new Label("Please provide the username and role you would like to change");
		pageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		
		// Input field to receive first the user then the role to be changed
		TextField userName = new TextField();
		userName.setPromptText("Username");
		
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll(Arrays.asList("admin", "student", "instructor", "staff", "reviewer"));
        
        Label message = new Label("");
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        // Add button
        Button addButton = new Button("Add role");
        addButton.setOnAction(e -> {
			String nameInput = userName.getText();
			String roleInput = roleComboBox.getValue();
			
			// Logic to add
			if(databaseHelper.doesUserExist(nameInput)) {
				if(databaseHelper.hasRole(nameInput, roleInput)) {
					message.setText("");
					errorLabel.setText("User " + nameInput + " already has role " + roleInput);
				} else {
					message.setText("Added " + roleInput + " role to user " + nameInput);
					errorLabel.setText("");
					databaseHelper.setUserRole(nameInput, roleInput, true);
				}
			} else {
				message.setText("");
				errorLabel.setText("User " + nameInput + " does not exist.");
			}
		});
        
        // Remove button
        Button removeButton = new Button("Remove role");
        removeButton.setOnAction(e -> {
			String nameInput = userName.getText();
			String roleInput = roleComboBox.getValue();
			
			// Logic to remove
			if(databaseHelper.doesUserExist(nameInput)) {
				if(!databaseHelper.hasRole(nameInput, roleInput)) {
					message.setText("");
					errorLabel.setText("User " + nameInput + " does not have role " + roleInput);
				} else {
					if (user.getUserName().equals(nameInput) && roleInput.equals("admin")) {
						message.setText("");
						errorLabel.setText("Cannot remove admin from yourself");
					} else {
						message.setText("Removed " + roleInput + " role from user " + nameInput);
						errorLabel.setText("");
						databaseHelper.setUserRole(nameInput, roleInput, false);
					}
				}
			} else {
				message.setText("");
				errorLabel.setText("User " + nameInput + " does not exist.");
			}
		});
		
		// Back button
		Button backButton = new Button("Back");
		backButton.setOnAction(a -> {
			new UserListingPage(databaseHelper).show(primaryStage, user);
		});
		VBox layout = new VBox(10);

		layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
		layout.getChildren().add(pageLabel);
		layout.getChildren().add(userName);
		layout.getChildren().add(roleComboBox);
		layout.getChildren().add(addButton);
		layout.getChildren().add(removeButton);
		layout.getChildren().add(backButton);
		layout.getChildren().add(message);
		layout.getChildren().add(errorLabel);

		Scene scene = new Scene(layout, 800, 400);

		// Set the scene to primary stage
		primaryStage.setScene(scene);
		primaryStage.setTitle("Role Change");
		primaryStage.show();
	}
}