package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.util.List;

/**
 * The StaffPrivateMessagesPage class provides a dedicated user interface for staff members
 * to view and create private messages. It enables staff to:
 * <ul>
 *   <li>Review private chat conversations between students and instructors.</li>
 *   <li>Initiate new private messages to instructors or other staff members.</li>
 *   <li>Maintain and refresh conversation histories.</li>
 * </ul>
 * This class fulfills the following user stories for the staff role epic:
 * <ol>
 *   <li>Staff are able to read private chats between students and instructors.</li>
 *   <li>Staff can choose to create private messages with instructors/staff members.</li>
 *   <li>Staff can read their private messages.</li>
 * </ol>
 *
 * @author 
 * @version 1.0
 */
public class StaffPrivateMessagesPage {

    private final DatabaseHelper databaseHelper;
    private User currentUser;
    
    private ListView<String> partnerListView;
    private ListView<String> messageListView;
    private TextField messageField;
    private Button sendButton;
    
    /**
     * Constructs a StaffPrivateMessagesPage using the specified DatabaseHelper.
     *
     * @param databaseHelper the helper object to perform database operations.
     */
    public StaffPrivateMessagesPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Displays the staff private messaging interface.
     *
     * @param primaryStage the primary stage for the application.
     * @param user the currently logged-in staff member.
     */
    public void show(Stage primaryStage, User user) {
        this.currentUser = user;
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        Label titleLabel = new Label("Staff Private Messaging");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create and populate the chat partners list
        partnerListView = new ListView<>();
        refreshChatPartners();
        partnerListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                refreshMessageList(newVal);
            }
        });
        
        messageListView = new ListView<>();
        messageField = new TextField();
        messageField.setPromptText("Type your message here...");
        sendButton = new Button("Send Message");
        sendButton.setOnAction(e -> sendMessage());
        
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            // Return to DiscussionBoardPage; adjust if needed.
            new DiscussionBoardPage(databaseHelper).show(primaryStage, currentUser);
        });
        
        layout.getChildren().addAll(titleLabel, 
                                    new Label("Select Chat Partner:"), partnerListView,
                                    new Label("Conversation:"), messageListView, 
                                    messageField, sendButton, backButton);
        
        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Messaging");
        primaryStage.show();
    }
    
    /**
     * Refreshes the list of available chat partners for the staff member.
     * This typically includes both students and instructors who have engaged in private messaging.
     */
    private void refreshChatPartners() {
        List<String> partners = databaseHelper.fetchChatPartners(currentUser.getUserName());
        partnerListView.getItems().setAll(partners);
    }
    
    /**
     * Refreshes the conversation for the selected chat partner.
     *
     * @param partner the username of the selected chat partner.
     */
    private void refreshMessageList(String partner) {
        List<String> messages = databaseHelper.fetchPrivateMessages(currentUser.getUserName(), partner);
        messageListView.getItems().setAll(messages);
    }
    
    /**
     * Sends a new private message to the currently selected chat partner.
     */
    private void sendMessage() {
        String partner = partnerListView.getSelectionModel().getSelectedItem();
        if (partner == null) {
            showAlert("Please select a chat partner.");
            return;
        }
        String text = messageField.getText().trim();
        if (text.isEmpty()) {
            showAlert("Message cannot be empty.");
            return;
        }
        databaseHelper.savePrivateMessage(currentUser.getUserName(), partner, text);
        messageField.clear();
        refreshMessageList(partner);
    }
    
    /**
     * Displays an alert dialog with the specified message.
     *
     * @param msg the message to be displayed in the alert.
     */
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Staff Messaging Alert");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
