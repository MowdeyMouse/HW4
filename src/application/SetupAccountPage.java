package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.setMaxWidth(250);
        
        TextField nameField = new TextField();
        nameField.setPromptText("Enter name");
        nameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode (Valid for 2 minutes)");
        inviteCodeField.setMaxWidth(250);
        
        // Labels to display error messages for invalid input or registration issues
        Label userErrLabel = new Label();
        Label passErrLabel = new Label();
        Label emailErrLabel = new Label();
        Label codeErrLabel = new Label();
        userErrLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        passErrLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        emailErrLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        codeErrLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        

        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String email = emailField.getText();
            String name = nameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();
            // retrieve error codes
            String userNameErr = UserNameRecognizer.checkForValidUserName(userName);
            String passwordErr = PasswordEvaluator.evaluatePassword(password);
            String emailErr = EmailEvaluator.evaluateEmail(email);
            
            userErrLabel.setText(userNameErr);
        	passErrLabel.setText(passwordErr);
        	emailErrLabel.setText(emailErr);
            
            try {
            	//check if the user name and password are valid 
            	if(userNameErr.equals("") && passwordErr.equals("") && emailErr.equals("")) {
            		// Check if the user already exists
            		if(!databaseHelper.doesUserExist(userName)) {
            		
            			// Validate the invitation code
            			if(databaseHelper.validateInvitationCode(code)) {
            			
            				// Create a new user and register them in the database
            				User user=new User(userName, email, name, password, "user");
            				databaseHelper.register(user);
            				databaseHelper.setUserRole(userName, "student", true);
		                
            				// Navigate to the Welcome Login Page
            				new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
            			}
            			else {
            				codeErrLabel.setText("Code is expired or invalid");
            			}
            		}
            		else {
            			userErrLabel.setText("This username is taken!! Please use another to setup an account");
            		}
            	}
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        // Back button
	    Button backToLoginButton = new Button("Back");
	    backToLoginButton.setOnAction(a -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        // added user name and password error labels to displayed box
        layout.getChildren().addAll(userNameField,emailField,passwordField,inviteCodeField, setupButton, backToLoginButton, userErrLabel, emailErrLabel, passErrLabel, codeErrLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}