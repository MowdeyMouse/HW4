package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

import databasePart1.DatabaseHelper;

/**
 * A standalone page for a reviewer to view only private messages.
 * No discussion board tabs are shown here.
 */
public class ReviewerPrivateMessagesPage {

    private final DatabaseHelper databaseHelper;
    private User currentUser;

    private ListView<String> studentListView;
    private ListView<String> messageListView;
    private TextField messageField;
    private Button sendButton;

    public ReviewerPrivateMessagesPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, User user) {
        this.currentUser = user;
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        Label titleLabel = new Label("Reviewer Private Messages");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Students who have chatted with me
        Label studentsLabel = new Label("Students who have chatted with you:");
        studentListView = new ListView<>();
        refreshStudentChatsList();

        // On selection, load conversation
        studentListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                refreshMessageList(newVal);
            }
        });

        messageListView = new ListView<>();

        messageField = new TextField();
        messageField.setPromptText("Type a message to this student...");
        sendButton = new Button("Send");
        sendButton.setOnAction(e -> sendMessageToStudent());

        // Button -> back to ReviewerHomePage
        Button backToHomeButton = new Button("Back to Reviewer Home");
        backToHomeButton.setOnAction(e -> {
            new ReviewerHomePage(databaseHelper).show(primaryStage, currentUser);
        });



        layout.getChildren().addAll(
            titleLabel,
            studentsLabel, studentListView,
            new Label("Conversation:"),
            messageListView,
            new Label("Write a message:"),
            messageField,
            sendButton,
            backToHomeButton
           
        );

        Scene scene = new Scene(layout, 600, 500);
        primaryStage.setTitle("Private Messages (Reviewer)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Populate the student list with anyone who has chatted with this reviewer
    private void refreshStudentChatsList() {
        List<String> students = databaseHelper.fetchChatPartners(currentUser.getUserName());
        studentListView.getItems().setAll(students);
    }

    // Load the conversation with a particular student
    private void refreshMessageList(String studentUserName) {
        List<String> conversation = databaseHelper.fetchPrivateMessages(currentUser.getUserName(), studentUserName);
        messageListView.getItems().setAll(conversation);
    }

    // Send a new message to the selected student
    private void sendMessageToStudent() {
        String studentUserName = studentListView.getSelectionModel().getSelectedItem();
        if (studentUserName == null) {
            showAlert("Please select a student to send the message to.");
            return;
        }
        String text = messageField.getText().trim();
        if (text.isEmpty()) {
            showAlert("Message cannot be empty.");
            return;
        }
        databaseHelper.savePrivateMessage(currentUser.getUserName(), studentUserName, text);
        messageField.clear();
        refreshMessageList(studentUserName);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
