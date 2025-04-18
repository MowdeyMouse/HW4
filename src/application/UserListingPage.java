package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;

import databasePart1.DatabaseHelper;

public class UserListingPage {
	/**
     * Displays the UserListing page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	private final DatabaseHelper databaseHelper;

    public UserListingPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show(Stage primaryStage, User user) {
	    
	    // Label to display the Listing Page's Title
	    Label pageLabel = new Label("System Users");
	    pageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
	    
	    // Create list of users
	    ArrayList<String> userList = new ArrayList<>();
        
		for (User myUser : databaseHelper.getAllUsers()) {
			String userName = myUser.getUserName();
			String tempUser = "";
			
			// Make roles string
			ArrayList<String> roles = databaseHelper.getUserRoles(userName);
			String roleString = String.join(", ", roles);
			if(roles.isEmpty()) {
				roleString = "no roles";
			}
			
			tempUser = ("UserName: " + userName + ", Name: " + myUser.getName() + ", Email: " + myUser.getEmail() + ", Roles: " + roleString);
			userList.add(tempUser);
		}
	    
	   	ListView<String> listView = new ListView<>();
	   	listView.getItems().addAll(userList);
	   		
	    VBox layout = new VBox(5);
	    
	    // Route to Role change
	    Button roleChangeButton = new Button("Change User Roles");
	    roleChangeButton.setOnAction(a -> {
	    	new RoleChange(databaseHelper).show(primaryStage, user);}
	    );
	    
	    // Back button
	    Button backButton = new Button("Back");
	    backButton.setOnAction(a -> {
	    	new AdminHomePage(databaseHelper).show(primaryStage, user);}
	    );
	    
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    layout.getChildren().add(pageLabel);
	    layout.getChildren().add(listView);
	    layout.getChildren().add(roleChangeButton);
	    layout.getChildren().add(backButton);
	    
	    Scene scene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("User List");
	    primaryStage.show();
    }
}
