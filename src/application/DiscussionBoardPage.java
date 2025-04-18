package application;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;

import databasePart1.DatabaseHelper; 
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;

// ADDED FOR PRIVATE MESSAGING
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DiscussionBoardPage {

    private final DatabaseHelper databaseHelper;
    private Questions questions;   
    private Answers answers;   
    private Reviews reviews;
    private Question currentQuestion;
    
    public TabPane tabPane;
    private Tab viewAllTab;
    private Tab answerQuestionsTab;
    private Tab askQuestionsTab;
    private Tab myReviewsTab;         

    // ADDED: allow turning off the private-messages tab for reviewers
    private boolean showReviewerPrivateMessages = true; 

    // Me
    private User currentUser;

    // UI for questions
    private ListView<Question> questionListView;
    private TextField createQuestionField;

    // text field for searching
    private TextField searchField;  
    private CheckBox myQuestionsCheckBox;
    private CheckBox unresolvedCheckBox;

    // UI for answers
    private Label questionDisplayLabel;
    private ListView<Answer> answerListView;
    private TextField answerField;
    private CheckBox acceptedAnswersCheckBox;
    private CheckBox readListChange;
    
    // Checkbox
    private CheckBox clarificationCheckBox;
    
    // UI for Reviews
    private ListView<Review> questionReviewListView;
    private ListView<Review> answerReviewListView;
    private TextArea reviewTextArea;
    private Button addQuestionReviewButton;
    private Button addAnswerReviewButton;
    private Button rateReviewerButton;
    private Button updateReviewButton;
    private Button deleteReviewButton;
    
    // My Reviews
    private ListView<Review> myReviewsListView;

    // ADDED FOR TRUSTED REVIEWER FILTER:
    private CheckBox filterTrustedOnlyCheckBox;

    // ADDED FOR PRIVATE MESSAGING:
    private Tab privateMessagingTab;       // The new tab
    private ListView<String> reviewerList; // For students or reviewers
    private ListView<String> messageList;  // Show messages
    private TextField messageField;        
    private Button sendMessageButton;

    // ADDED FOR TRUSTED REVIEWERS MANAGEMENT:
    private Tab manageTrustedTab;
    private ListView<String> allReviewersListView;
    private ListView<String> trustedReviewersListView;
    private Button addTrustedButton;
    private Button removeTrustedButton;

    // EXISTING CONSTRUCTOR (FOR STUDENTS, ETC.) - keeps PM tab on by default
    public DiscussionBoardPage(DatabaseHelper databaseHelper) {
        this(databaseHelper, true);
    }

    // NEW CONSTRUCTOR letting us hide the PM tab for reviewers
    public DiscussionBoardPage(DatabaseHelper databaseHelper, boolean showReviewerPrivateMessages) {
        this.databaseHelper = databaseHelper;
        this.showReviewerPrivateMessages = showReviewerPrivateMessages;
    }
    
    public void show(Stage primaryStage, User user) {
    	this.currentUser = user;
        this.questions = new Questions(databaseHelper);
        this.answers = new Answers(databaseHelper);
        this.reviews = new Reviews(databaseHelper);
        
        tabPane = new TabPane();
        
        // -------------------------------------------
        // CREATE THE FIRST 3 TABS IN A KNOWN ORDER
        // -------------------------------------------
        
        // Tab 1
        viewAllTab = new Tab("View All Questions");
        viewAllTab.setClosable(false);
        VBox tab1Layout = new VBox(10);
        tab1Layout.setAlignment(Pos.TOP_CENTER);
        tab1Layout.setStyle("-fx-padding: 10;");
        
        // SEARCH SECTION
        Label searchLabel = new Label("Search Questions by Keyword:");
        searchField = new TextField();
        searchField.setPromptText("e.g., 'due' or 'exam'");
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.trim().isEmpty()) {
                refreshQuestionList();
            } else {
                List<Question> found = questions.searchQuestions(newText.trim());
                questionListView.getItems().setAll(found);
            }
        });
        
        HBox questionCheckBoxes = new HBox(8);
        questionCheckBoxes.setAlignment(Pos.CENTER);
        
        myQuestionsCheckBox = new CheckBox("My Questions");
        myQuestionsCheckBox.setOnAction(e -> refreshQuestionList());
        
        unresolvedCheckBox = new CheckBox("Only Unresolved");
        unresolvedCheckBox.setOnAction(e -> refreshQuestionList());
        
        questionCheckBoxes.getChildren().addAll(myQuestionsCheckBox, unresolvedCheckBox);
        
        // LIST OF ALL QUESTIONS
        questionListView = new ListView<>();
        refreshQuestionList();  // load from DB
        questionListView.setOnMouseClicked(event -> {
            // Get the currently clicked item
            Question selected = questionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                currentQuestion = selected;
                updateQuestionPage(selected);
                refreshQuestionReviewList(selected);

                tabPane.getSelectionModel().select(answerQuestionsTab);
            }
        });
        
        Button backButton1 = new Button("Back");
        backButton1.setOnAction(a -> {
            // If the reviewer is in here, going "Back" might return to ReviewerHomePage
            if ("reviewer".equalsIgnoreCase(user.getRole())) {
                new ReviewerHomePage(databaseHelper).show(primaryStage, user);
            } else {
                HomePageNavigator.navigateToRoleHome(databaseHelper, primaryStage, user, user.getRole());
            }
        });
        
        tab1Layout.getChildren().addAll(
            searchLabel,
            searchField,
            questionCheckBoxes,
            questionListView,
            backButton1
        );
        viewAllTab.setContent(tab1Layout);
        
        // Tab 2
        answerQuestionsTab = new Tab("Answer Questions");
        answerQuestionsTab.setClosable(false);
        VBox tab2Layout = new VBox(10);
        tab2Layout.setAlignment(Pos.TOP_CENTER);
        tab2Layout.setStyle("-fx-padding: 10;");
        
        // Display question
        questionDisplayLabel = new Label();
        questionDisplayLabel.setText("Select a question from the 'View All Questions' tab");
        questionDisplayLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // UPDATE / DELETE for question
        HBox questionEditButtons = new HBox(8);
        questionEditButtons.setAlignment(Pos.CENTER);
        Button updateQuestionButton = new Button("Update Question");
        updateQuestionButton.setOnAction(e -> showUpdateQuestionDialog());
        Button deleteQuestionButton = new Button("Delete Question");
        deleteQuestionButton.setOnAction(e -> handleDeleteQuestion());
        
        questionEditButtons.getChildren().addAll(updateQuestionButton, deleteQuestionButton);
        
        // ANSWERS DISPLAY
        Label answersLabel = new Label("Answers");
        answersLabel.setStyle("-fx-font-weight: bold;");
        
        acceptedAnswersCheckBox = new CheckBox("Accepted Answers");
        acceptedAnswersCheckBox.setOnAction(e -> handleAcceptedListChange());
        readListChange = new CheckBox("Only Unread");
        readListChange.setOnAction(e -> handleReadListChange());

        HBox answerFilterBox = new HBox(8);
        answerFilterBox.setAlignment(Pos.CENTER);
        answerFilterBox.getChildren().addAll(acceptedAnswersCheckBox, readListChange);
        
        answerListView = new ListView<>();
        
        // Listener for reviews
        answerListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                refreshAnswerReviewList(newSelection); // Refresh reviews for selected answer
                if (currentUser.getRole().equals("reviewer")) {
                    addAnswerReviewButton.setDisable(false);
                }
            } else {
                answerReviewListView.getItems().clear(); // Clear answer reviews if no answer selected
                if (currentUser.getRole().equals("reviewer")) {
                    addAnswerReviewButton.setDisable(true);
                }
            }
        });
        
        // UPDATE / DELETE for answer
        HBox answerEditButtons = new HBox(8);
        Button markAsResolvingButton = new Button("Mark as accepted answer");
        markAsResolvingButton.setOnAction(e -> handleAcceptAnswer());
        Button readAnswerButton = new Button("Mark Read");
        readAnswerButton.setOnAction(e -> handleReadAnswer());
        answerEditButtons.setAlignment(Pos.CENTER);
        Button updateAnswerButton = new Button("Update Answer");
        updateAnswerButton.setOnAction(e -> showUpdateAnswerDialog());
        Button deleteAnswerButton = new Button("Delete Answer");
        deleteAnswerButton.setOnAction(e -> handleDeleteAnswer());
        answerEditButtons.getChildren().addAll(markAsResolvingButton, readAnswerButton, updateAnswerButton, deleteAnswerButton);
        
        // ANSWER CREATION SECTION
        Label answerLabel = new Label("Answer the Selected Question");
        answerField = new TextField();
        answerField.setPromptText("Type your answer here...");
        clarificationCheckBox = new CheckBox("Request for Clarification");

        Button answerButton = new Button("Answer Question");
        answerButton.setOnAction(e -> handlePostAnswer(user));
        
        // Reviews section
        Label reviewSectionLabel = new Label("Reviews");
        reviewSectionLabel.setStyle("-fx-font-weight: bold;");
        
        // ADDED FOR TRUSTED REVIEWER FILTER:
        filterTrustedOnlyCheckBox = new CheckBox("Filter by Trusted Reviewers Only");
        filterTrustedOnlyCheckBox.setOnAction(e -> {
            refreshQuestionReviewList(currentQuestion);
            if (answerListView.getSelectionModel().getSelectedItem() != null) {
                refreshAnswerReviewList(answerListView.getSelectionModel().getSelectedItem());
            }
        });

        Label questionReviewsLabel = new Label("Reviews for Selected Question:");
        questionReviewListView = new ListView<>();
        Label answerReviewsLabel = new Label("Reviews for Selected Answer:");
        answerReviewListView = new ListView<>();
        
        ChangeListener<Review> reviewSelectionListener = (obs, oldR, newR) -> {
        	boolean reviewSelected = questionReviewListView.getSelectionModel().getSelectedItem() != null 
                || answerReviewListView.getSelectionModel().getSelectedItem() != null;
        	if (currentUser.getRole().equals("reviewer")) {
        		updateReviewButton.setDisable(!reviewSelected);
        		deleteReviewButton.setDisable(!reviewSelected);
        	} else if (currentUser.getRole().equals("student")) {
                rateReviewerButton.setDisable(!reviewSelected);
            }
        };
        
        questionReviewListView.getSelectionModel().selectedItemProperty().addListener(reviewSelectionListener);
        answerReviewListView.getSelectionModel().selectedItemProperty().addListener(reviewSelectionListener);
        
        // Make it so only one can be selected
        questionReviewListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                answerReviewListView.getSelectionModel().clearSelection();
            }
        });

        answerReviewListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                questionReviewListView.getSelectionModel().clearSelection();
            }
        });
        
        VBox addReviewBox = new VBox(5);
        addReviewBox.setAlignment(Pos.CENTER_LEFT);
        Label addReviewLabel = new Label("Add/Edit Review:");
        reviewTextArea = new TextArea();
        reviewTextArea.setPromptText("Enter review text...");
        reviewTextArea.setPrefRowCount(2);
        
        HBox reviewInputControls = new HBox(5, reviewTextArea);
        HBox reviewActionButtons = createReviewActionButtons();
        
        // Rate Reviewers
        rateReviewerButton = new Button("Rate Reviewer");
        rateReviewerButton.setOnAction(e -> handleRateReviewer());
        rateReviewerButton.setDisable(true);

        if (currentUser.getRole().equals("reviewer")) {
            addReviewBox.getChildren().addAll(addReviewLabel, reviewInputControls, reviewActionButtons);
        }
        
        Button backButton2 = new Button("Back");
        backButton2.setOnAction(a -> {
            tabPane.getSelectionModel().select(0);
        });
        
        tab2Layout.getChildren().addAll(
            questionDisplayLabel,
            questionEditButtons,
            answersLabel,
            answerFilterBox,
            answerListView,
            answerEditButtons,
            answerLabel,
            answerField,
            clarificationCheckBox,
            answerButton,
            reviewSectionLabel,
            filterTrustedOnlyCheckBox, 
            questionReviewsLabel, questionReviewListView,
            answerReviewsLabel, answerReviewListView,
            addReviewBox,
            rateReviewerButton,
            backButton2
        );
        answerQuestionsTab.setContent(tab2Layout);
        
        // Tab 3
        askQuestionsTab = new Tab("Ask New Question");
        askQuestionsTab.setClosable(false);
        VBox tab3Layout = new VBox(10);
        tab3Layout.setAlignment(Pos.TOP_CENTER);
        tab3Layout.setStyle("-fx-padding: 10;");
        
        // CREATE QUESTION SECTION
        Label createQLabel = new Label("Ask a New Question");
        createQuestionField = new TextField();
        createQuestionField.setPromptText("Type your question here...");

        Button createButton = new Button("Create Question");
        createButton.setOnAction(e -> handleCreateQuestion(user));
        
        Button backButton3 = new Button("Back");
        backButton3.setOnAction(a -> {
            tabPane.getSelectionModel().select(0);
        });
        
        tab3Layout.getChildren().addAll(
            createQLabel,
            createQuestionField,
            createButton,
            backButton3
        );
        askQuestionsTab.setContent(tab3Layout);

        // Add the three main tabs
        tabPane.getTabs().addAll(viewAllTab, answerQuestionsTab, askQuestionsTab);

        // If the user is a reviewer, add "My Reviews"
        if (currentUser.getRole().equals("reviewer")) {
            myReviewsTab = new Tab("My Reviews");
            myReviewsTab.setClosable(false);
            VBox tab4Layout = new VBox(10);
            tab4Layout.setAlignment(Pos.TOP_CENTER);
            tab4Layout.setStyle("-fx-padding: 10;");
            
            Label myReviewsLabel = new Label("Reviews You Have Written");
            myReviewsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
            myReviewsListView = new ListView<>();
            
            Button backButton4 = new Button("Back");
            backButton4.setOnAction(a -> {
                // Return to the 'View All Questions' tab or home page
                tabPane.getSelectionModel().select(0);
            });
            
            tab4Layout.getChildren().addAll(
                myReviewsLabel,
                myReviewsListView,
                backButton4
            );
            myReviewsTab.setContent(tab4Layout);

            tabPane.getTabs().add(myReviewsTab);
            refreshMyReviewsList();
        }

        // If "student" or "reviewer" AND showReviewerPrivateMessages == true, add private-messaging tab
        if ((("student".equalsIgnoreCase(currentUser.getRole()) 
            || "reviewer".equalsIgnoreCase(currentUser.getRole())))
            && showReviewerPrivateMessages) 
        {
            privateMessagingTab = new Tab("Private Messages");
            privateMessagingTab.setClosable(false);
            VBox pmLayout = new VBox(10);
            pmLayout.setStyle("-fx-padding: 10;");
            pmLayout.setAlignment(Pos.TOP_CENTER);

            if ("student".equalsIgnoreCase(currentUser.getRole())) {
                Label pickReviewerLabel = new Label("Select a Reviewer to Chat With:");
                reviewerList = new ListView<>();
                List<String> allReviewerNames = databaseHelper.getAllUsers().stream()
                    .filter(u -> databaseHelper.hasRole(u.getUserName(), "reviewer"))
                    .map(User::getUserName)
                    .collect(Collectors.toList());
                reviewerList.getItems().addAll(allReviewerNames);

                messageList = new ListView<>();
                messageField = new TextField();
                messageField.setPromptText("Type your private message...");
                sendMessageButton = new Button("Send Message");
                sendMessageButton.setOnAction(e -> sendPrivateMessage());

                reviewerList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        refreshMessageList(newVal);
                    }
                });

                pmLayout.getChildren().addAll(
                    pickReviewerLabel,
                    reviewerList,
                    messageList,
                    messageField,
                    sendMessageButton
                );
            }
            else {
                // This is for reviewer if they wanted to see PM (but we are skipping that by default)
                Label myChatsLabel = new Label("Private Messages From Students");
                reviewerList = new ListView<>();
                refreshStudentChatsList(); 
                
                messageList = new ListView<>();
                messageField = new TextField();
                messageField.setPromptText("Type your reply...");
                sendMessageButton = new Button("Reply");
                sendMessageButton.setOnAction(e -> sendPrivateMessage());

                reviewerList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        refreshMessageList(newVal);
                    }
                });

                pmLayout.getChildren().addAll(
                    myChatsLabel, 
                    reviewerList, 
                    messageList, 
                    messageField, 
                    sendMessageButton
                );
            }
            privateMessagingTab.setContent(pmLayout);
            tabPane.getTabs().add(privateMessagingTab);
        }

        // If student, add “Manage Trusted Reviewers”
        if ("student".equalsIgnoreCase(currentUser.getRole())) {
            manageTrustedTab = new Tab("Manage Trusted Reviewers");
            manageTrustedTab.setClosable(false);
            VBox trustLayout = new VBox(10);
            trustLayout.setStyle("-fx-padding: 10;");
            trustLayout.setAlignment(Pos.TOP_CENTER);

            Label allLabel = new Label("All Reviewers:");
            allReviewersListView = new ListView<>();
            Label trustedLabel = new Label("Your Trusted Reviewers:");
            trustedReviewersListView = new ListView<>();

            refreshTrustedReviewerLists();

            addTrustedButton = new Button("Add to Trusted");
            addTrustedButton.setOnAction(e -> addTrustedReviewer());
            removeTrustedButton = new Button("Remove from Trusted");
            removeTrustedButton.setOnAction(e -> removeTrustedReviewer());

            trustLayout.getChildren().addAll(
                allLabel, allReviewersListView,
                trustedLabel, trustedReviewersListView,
                addTrustedButton, removeTrustedButton
            );
            manageTrustedTab.setContent(trustLayout);

            tabPane.getTabs().add(manageTrustedTab);
        }

        Scene scene = new Scene(tabPane, 520, 750);
        primaryStage.setScene(scene);
        if ("reviewer".equalsIgnoreCase(currentUser.getRole()) && !showReviewerPrivateMessages) {
            primaryStage.setTitle("Discussion Board (Reviewer) - No PM");
        } else {
            primaryStage.setTitle("Discussion Board");
        }
        primaryStage.show();
        
        setupReviewerControlsVisibility();
        
     // Add a new tab for staff private messaging if the current user is a staff member.
        if ("staff".equalsIgnoreCase(currentUser.getRole())) {
            Button staffPMButton = new Button("Staff Private Messaging");
            staffPMButton.setOnAction(e -> {
                new StaffPrivateMessagesPage(databaseHelper).show(primaryStage, currentUser);
            });
            Tab staffPMTab = new Tab("Staff Messaging");
            VBox staffLayout = new VBox(10);
            staffLayout.setStyle("-fx-padding: 10; -fx-alignment: center;");
            staffLayout.getChildren().add(staffPMButton);
            staffPMTab.setContent(staffLayout);
            tabPane.getTabs().add(staffPMTab);
        }

    }

    // ADDED FOR TRUSTED REVIEWERS:
    private void refreshTrustedReviewerLists() {
        List<String> allReviewerNames = databaseHelper.getAllUsers().stream()
            .filter(u -> databaseHelper.hasRole(u.getUserName(), "reviewer"))
            .map(User::getUserName)
            .collect(Collectors.toList());

        List<String> trusted = databaseHelper.fetchTrustedReviewers(currentUser.getUserName());
        currentUser.setTrustedReviewers(trusted);

        allReviewersListView.getItems().setAll(allReviewerNames);
        trustedReviewersListView.getItems().setAll(trusted);
    }

    private void addTrustedReviewer() {
        String selected = allReviewersListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a reviewer from the 'All Reviewers' list.");
            return;
        }
        if (currentUser.isTrustedReviewer(selected)) {
            showAlert("That reviewer is already in your trusted list.");
            return;
        }
        databaseHelper.saveTrustedReviewer(currentUser.getUserName(), selected);
        currentUser.addTrustedReviewer(selected);
        refreshTrustedReviewerLists();
    }

    private void removeTrustedReviewer() {
        String selected = trustedReviewersListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a reviewer from your 'Trusted Reviewers' list.");
            return;
        }
        databaseHelper.removeTrustedReviewer(currentUser.getUserName(), selected);
        currentUser.removeTrustedReviewer(selected);
        refreshTrustedReviewerLists();
    }

    // ADDED FOR PRIVATE MESSAGING:
    private void refreshStudentChatsList() {
        List<String> students = databaseHelper.fetchChatPartners(currentUser.getUserName());
        reviewerList.getItems().setAll(students);
    }

    private void refreshMessageList(String otherUser) {
        List<String> conversation = databaseHelper.fetchPrivateMessages(currentUser.getUserName(), otherUser);
        messageList.getItems().setAll(conversation);
    }

    private void sendPrivateMessage() {
        String otherUser = reviewerList.getSelectionModel().getSelectedItem();
        if (otherUser == null) {
            showAlert("Select a user to send a message to.");
            return;
        }
        String text = messageField.getText().trim();
        if (text.isEmpty()) {
            showAlert("Message cannot be empty.");
            return;
        }
        databaseHelper.savePrivateMessage(currentUser.getUserName(), otherUser, text);
        messageField.clear();
        refreshMessageList(otherUser);
    }

    private void updateQuestionPage(Question q) {
        questionDisplayLabel.setText(q.getText());
        List<Answer> ansList = answers.getAnswersByQuestionText(q.getText());
        answerListView.getItems().setAll(ansList);
    }
    
    //  CREATE QUESTION -> DB
    private void handleCreateQuestion(User user) {
        String author = user.getUserName();
        if (author.isEmpty()) {
            showAlert("Name cannot be empty.");
            return;
        }

        String qText = createQuestionField.getText().trim();
        if (qText.isEmpty()) {
            showAlert("Please enter non-empty question text.");
            return;
        }
        try {
            Question newQ = new Question(qText, author, false);
            questions.addQuestion(newQ);
            createQuestionField.clear();
            refreshQuestionList();
            
            currentQuestion = newQ;
            updateQuestionPage(newQ);
            
            refreshQuestionReviewList(newQ);
            if (currentUser.getRole().equals("reviewer")) {
            	addQuestionReviewButton.setDisable(false);
            }
            tabPane.getSelectionModel().select(answerQuestionsTab);
        } catch (IllegalArgumentException ex) {
            showAlert("Error: " + ex.getMessage());
        }
    }

    private void refreshQuestionList() {
        List<Question> all = questions.getAllQuestions();
        
        // Apply search filter if any
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            all = questions.searchQuestions(keyword);
        }
        
        // Filter unresolved questions if selected
        if (unresolvedCheckBox.isSelected()) {
            all = all.stream().filter(q -> !q.getResolved()).collect(Collectors.toList());
        }
        
        // Filter by "My Questions" if selected
        if (myQuestionsCheckBox.isSelected()) {
            all = all.stream().filter(q -> q.getAuthor()
            		.equals(currentUser.getUserName())).collect(Collectors.toList());
        }
        
        questionListView.getItems().setAll(all);
    }
    
    private void showUnresolvedQuestionList() {
        List<Question> all = questions.getUnresolvedQuestions();
        questionListView.getItems().setAll(all);
    }

    private void handleResolveListChange() {
    	if (unresolvedCheckBox.isSelected()) {
            showUnresolvedQuestionList();
        } else {
            refreshQuestionList();
        }
    }
    
    //  UPDATE QUESTION (POPUP)
    private void showUpdateQuestionDialog() {
        Question selected = currentQuestion;
        if (selected == null) {
            showAlert("Please select a question to update.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getText());
        dialog.setTitle("Update Question");
        dialog.setHeaderText(null);
        dialog.setContentText("New question text:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newText = result.get().trim();
            if (newText.isEmpty()) {
                showAlert("Please enter a new text for the question.");
                return;
            }
            boolean success = questions.updateQuestion(selected.getText(), newText);
            if (!success) {
                showAlert("Failed to update question. Possibly text not found?");
            }
            refreshQuestionList();
            refreshAnswerListForSelectedQuestion();
        }
    }

    //  DELETE QUESTION -> DB (WITH CONFIRMATION)
    private void handleDeleteQuestion() {
        Question selected = currentQuestion;
        if (selected == null) {
            showAlert("Please select a question to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this question?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean removed = questions.removeQuestion(selected.getText());
            if (!removed) {
                showAlert("No question found with text: " + selected.getText());
            }
            refreshQuestionList();
            answerListView.getItems().clear();
            questionDisplayLabel.setText("Select a question from the 'View All Questions' tab");
            answerReviewListView.getItems().clear();
            questionReviewListView.getItems().clear();
            currentQuestion = null;
            tabPane.getSelectionModel().select(0);
        }
    }
    
    private void handleResolveQuestion() {
        Question selected = currentQuestion;
        if (selected == null) {
            showAlert("Please select a question to resolve.");
            return;
        }
        boolean removed = questions.setResolveQuestion(selected.getText(), !selected.getResolved());
        if (!removed) {
            showAlert("No question found with text: " + selected.getText());
        }
        refreshQuestionList();
    }

    //  ANSWER -> DB
    private void handlePostAnswer(User user) {
        Question selected = currentQuestion;
        if (selected == null) {
            showAlert("Please select a question to answer.");
            return;
        }

        String author = user.getUserName();
        if (author.isEmpty()) {
            showAlert("Name cannot be empty.");
            return;
        }

        String ansText = answerField.getText().trim();
        if (ansText.isEmpty()) {
            showAlert("Please enter some text for your answer.");
            return;
        }
        
        boolean isClarification = clarificationCheckBox.isSelected();
        
        try {
            Answer newAns = new Answer(selected.getText(), ansText, author, false, false, isClarification);
            answers.addAnswer(newAns);
            answerField.clear();
            clarificationCheckBox.setSelected(false);
            refreshAnswerListForSelectedQuestion();
        } catch (IllegalArgumentException ex) {
            showAlert("Error: " + ex.getMessage());
        }
    }

    private String refreshAnswerListForSelectedQuestion() {
        Question selected = currentQuestion;
        if (selected == null) {
            answerListView.getItems().clear();
            return "Resolve Question";
        }
        List<Answer> ansList = answers.getAnswersByQuestionText(selected.getText());
        answerListView.getItems().setAll(ansList);
        if (selected.getResolved()) {
        	return "Unresolve Question";
        }
        return "Resolve Question";
    }
    
    private String refreshReadButtonText() {
    	Answer selected = answerListView.getSelectionModel().getSelectedItem();
    	if (selected == null) {
    		return "Read Anwswer";
    	}
    	if (selected.getRead()) {
    		return "Mark Unread";
    	}
    	else {
    		return "Mark Read";
    	}
    }
    
    private void showAcceptedAnswerListForSelectedQuestion() {
        Question selected = currentQuestion;
        if (selected == null) {
            answerListView.getItems().clear();
            return;
        }
        List<Answer> ansList = answers.getAcceptedAnswersByQuestionText(selected.getText());
        answerListView.getItems().setAll(ansList);
    }
    
    private void showUnreadAnswerListForSelectedQuestion() {
        Question selected = currentQuestion;
        if (selected == null) {
            answerListView.getItems().clear();
            return;
        }
        List<Answer> ansList = answers.getUnreadAnswersByQuestionText(selected.getText());
        answerListView.getItems().setAll(ansList);
    }
    
    private void handleAcceptedListChange() {
    	if(acceptedAnswersCheckBox.isSelected()) {
    		readListChange.setSelected(false);
    		showAcceptedAnswerListForSelectedQuestion();
    	}
    	else {
    		refreshAnswerListForSelectedQuestion();
    	}
    }
    
    private void handleReadListChange() {
    	if(readListChange.isSelected()) {
    		acceptedAnswersCheckBox.setSelected(false);
    		showUnreadAnswerListForSelectedQuestion();
    	}
    	else {
    		refreshAnswerListForSelectedQuestion();
    	}
    }

    //  UPDATE ANSWER (POPUP DIALOG)
    private void showUpdateAnswerDialog() {
        Answer selected = answerListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an answer to update.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getText());
        dialog.setTitle("Update Answer");
        dialog.setHeaderText(null);
        dialog.setContentText("New answer text:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newText = result.get().trim();
            if (newText.isEmpty()) {
                showAlert("Please enter a new text for the answer.");
                return;
            }
            boolean success = answers.updateAnswer(selected.getText(), newText);
            if (!success) {
                showAlert("Failed to update answer. Possibly text not found?");
            }
            refreshAnswerListForSelectedQuestion();
        }
    }
    
    private void handleAcceptAnswer() {
        Answer selected = answerListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an answer to accept.");
            return;
        }
        boolean accepted = answers.acceptAnswer(selected.getText(), true);
        if (!accepted) {
            showAlert("Failed to mark answer as accepted: " + selected.getText());
        }
        questions.setResolveQuestion(currentQuestion.getText(), true);
        refreshQuestionList();
        refreshAnswerListForSelectedQuestion();
    }
    
    private void handleReadAnswer() {
    	Answer selected = answerListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an answer to read.");
            return;
        }
        boolean read = answers.readAnswer(selected.getText(), !selected.getRead());
        if (!read) {
            showAlert("Failed to read answer with text: " + selected.getText());
        }
        refreshAnswerListForSelectedQuestion();
    }

    //  DELETE ANSWER -> DB (WITH CONFIRMATION)
    private void handleDeleteAnswer() {
        Answer selected = answerListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an answer to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this answer?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean removed = answers.removeAnswer(selected.getText());
            if (!removed) {
                showAlert("Failed to delete answer with text: " + selected.getText());
            }
            refreshAnswerListForSelectedQuestion();
        }
    }
    
    // REVIEWS
    private HBox createReviewActionButtons() {
        HBox reviewActionButtons = new HBox(8);
        reviewActionButtons.setAlignment(Pos.CENTER);

        addQuestionReviewButton = new Button("Review Question");
        addQuestionReviewButton.setOnAction(e -> handleAddReview("QUESTION"));
        addQuestionReviewButton.setDisable(false);

        addAnswerReviewButton = new Button("Review Answer");
        addAnswerReviewButton.setOnAction(e -> handleAddReview("ANSWER"));
        addAnswerReviewButton.setDisable(answerListView.getSelectionModel().getSelectedItem() == null);

        updateReviewButton = new Button("Update Review");
        updateReviewButton.setOnAction(e -> handleUpdateReview());
        updateReviewButton.setDisable(true);

        deleteReviewButton = new Button("Delete Review");
        deleteReviewButton.setOnAction(e -> handleDeleteReview());
        deleteReviewButton.setDisable(true);
        
        boolean isStudent = currentUser.getRole().equals("student");
        if (rateReviewerButton != null) {
        	rateReviewerButton.setVisible(isStudent);
        	rateReviewerButton.setManaged(isStudent);
        }

        reviewActionButtons.getChildren().addAll(addQuestionReviewButton, addAnswerReviewButton, updateReviewButton, deleteReviewButton);
        return reviewActionButtons;
   }

   private void setupReviewerControlsVisibility() {
       boolean isReviewer = currentUser.getRole().equals("reviewer");
       if (addQuestionReviewButton != null) {
           addQuestionReviewButton.setVisible(isReviewer);
           addQuestionReviewButton.setManaged(isReviewer);
           addAnswerReviewButton.setVisible(isReviewer);
           addAnswerReviewButton.setManaged(isReviewer);
           updateReviewButton.setVisible(isReviewer);
           updateReviewButton.setManaged(isReviewer);
           deleteReviewButton.setVisible(isReviewer);
           deleteReviewButton.setManaged(isReviewer);
       }
    }
   
    private void refreshQuestionReviewList(Question question) {
        if (question == null) {
            questionReviewListView.getItems().clear();
            return;
        }
        List<Review> reviewsList = reviews.getReviewsForQuestion(question.getText());
        // filter by trusted if the user is a student and the box is checked
        if ("student".equalsIgnoreCase(currentUser.getRole()) && filterTrustedOnlyCheckBox.isSelected()) {
            reviewsList = reviewsList.stream()
                .filter(r -> currentUser.isTrustedReviewer(r.getAuthor()))
                .collect(Collectors.toList());
        }
        questionReviewListView.getItems().setAll(reviewsList);
    }

    private void refreshAnswerReviewList(Answer answer) {
        if (answer == null) {
            answerReviewListView.getItems().clear();
            return;
        }
        List<Review> reviewsList = reviews.getReviewsForAnswer(answer.getText());
        // filter by trusted if the user is a student and the box is checked
        if ("student".equalsIgnoreCase(currentUser.getRole()) && filterTrustedOnlyCheckBox.isSelected()) {
            reviewsList = reviewsList.stream()
                .filter(r -> currentUser.isTrustedReviewer(r.getAuthor()))
                .collect(Collectors.toList());
        }
        answerReviewListView.getItems().setAll(reviewsList);
    }
    
    private void refreshMyReviewsList() {
    	List<Review> myReviews = reviews.getReviewsByAuthor(currentUser.getUserName());
    	myReviewsListView.getItems().setAll(myReviews);
    }
    
    // Add reviews
    private void handleAddReview(String targetType) {
        String reviewText = reviewTextArea.getText().trim();
        if (reviewText.isEmpty()) {
            showAlert("Please enter review text.");
            return;
        }

        String targetId = null;
        if ("QUESTION".equals(targetType)) {
            if (currentQuestion != null) {
                targetId = currentQuestion.getText();
            } else {
                showAlert("No question selected to review.");
                return;
            }
        } else if ("ANSWER".equals(targetType)) {
            Answer selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
            if (selectedAnswer != null) {
                targetId = selectedAnswer.getText();
            } else {
                showAlert("No answer selected to review.");
                return;
            }
        } else {
            showAlert("Invalid target type for review.");
            return;
        }

        Review newReview = new Review(targetId, targetType, reviewText, currentUser.getUserName());
        boolean success = reviews.addReview(newReview);

        if (success) {
            showAlert("Review added successfully.");
            reviewTextArea.clear();
            if ("QUESTION".equals(targetType)) {
                refreshQuestionReviewList(currentQuestion);
            } else {
                refreshAnswerReviewList(answerListView.getSelectionModel().getSelectedItem());
            }
        } else {
            showAlert("Failed to add review.");
        }
    }
    
    // Update review text
    private void handleUpdateReview() {
        Review selectedReview = getSelectedReview();
        if (selectedReview == null) {
            showAlert("Please select a review to update.");
            return;
        }
        
        if (!selectedReview.getAuthor().equals(currentUser.getUserName())) {
             showAlert("You can only update your own reviews.");
             return;
        }

        String newText = reviewTextArea.getText().trim();
        if (newText.isEmpty()) {
             showAlert("Review text cannot be empty.");
             return;
        }
        boolean success = reviews.updateReview(selectedReview.getId(), newText);
        if (success) {
            showAlert("Review updated.");
            if ("QUESTION".equals(selectedReview.getTargetType()))
                refreshQuestionReviewList(currentQuestion);
            else
                refreshAnswerReviewList(answerListView.getSelectionModel().getSelectedItem());
        } else {
            showAlert("Failed to update review.");
        }
    }
    
    // Delete reviews
    private void handleDeleteReview() {
        Review selectedReview = getSelectedReview();
        if (selectedReview == null) {
            showAlert("Please select a review to delete.");
            return;
        }
        
        if (!selectedReview.getAuthor().equals(currentUser.getUserName())) {
             showAlert("You can only delete your own reviews.");
             return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this review?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            boolean success = reviews.removeReview(selectedReview.getId());
            if (success) {
                showAlert("Review deleted.");
                if ("QUESTION".equals(selectedReview.getTargetType()))
                    refreshQuestionReviewList(currentQuestion);
                else
                    refreshAnswerReviewList(answerListView.getSelectionModel().getSelectedItem());
            } else {
                showAlert("Failed to delete review.");
            }
        }
    }
    
    private void handleRateReviewer() {
        Review selectedReview = getSelectedReview();
        if (selectedReview == null) {
            showAlert("Please select a review to rate its author.");
            return;
        }

        if (!currentUser.getRole().equals("student")) {
            showAlert("Only students can rate reviewers.");
            return;
        }

        String reviewerUserName = selectedReview.getAuthor();
        String studentUserName = currentUser.getUserName();

        // Fetch previous rating if exists (returns -1 if not found)
        int previousRating = databaseHelper.getReviewerWeight(studentUserName, reviewerUserName);

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(previousRating == -1 ? 0 : previousRating, Arrays.asList(0, 1, 2, 3, 4, 5));
        dialog.setTitle("Rate Reviewer");
        dialog.setHeaderText("Rate reviewer: " + reviewerUserName);
        
        String contentText = "Select a weight (0-5) for this reviewer.";
        if (previousRating != -1) {
            contentText += "\nYour previous rating was: " + previousRating;
        }
        dialog.setContentText(contentText);

        Optional<Integer> result = dialog.showAndWait();

        result.ifPresent(newRating -> {
            boolean success = databaseHelper.setReviewerWeight(studentUserName, reviewerUserName, newRating);
            if (success) {
                showAlert("Reviewer rating saved successfully.");
                if (questionReviewListView.getSelectionModel().getSelectedItem() != null) {
                    refreshQuestionReviewList(currentQuestion);
                } else if (answerReviewListView.getSelectionModel().getSelectedItem() != null) {
                    refreshAnswerReviewList(answerListView.getSelectionModel().getSelectedItem());
                }
            } else {
                showAlert("Failed to save reviewer rating.");
            }
        });
    }

    // Helper to get the selected review from either list view
    private Review getSelectedReview() {
        Review selected = questionReviewListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            return selected;
        }
        return answerReviewListView.getSelectionModel().getSelectedItem();
    }
    
    //  POP-UP FOR NAME
    private String askForName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Your Name");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter your name:");

        Optional<String> result = dialog.showAndWait();
        return result.orElse("").trim();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
