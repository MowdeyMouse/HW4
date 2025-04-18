package application;

import javafx.application.Platform;
import javafx.stage.Stage;

import databasePart1.DatabaseHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The StaffRoleTests class contains JUnit tests that verify the staff-related functionalities
 * of the application. These tests ensure that staff users can:
 * <ul>
 *   <li>View discussion board questions.</li>
 *   <li>View answers to discussion board questions.</li>
 *   <li>Read private chat messages.</li>
 *   <li>Send private messages.</li>
 *   <li>Display the staff private messaging user interface without errors.</li>
 * </ul>
 * This test suite validates the proper functioning of the staff role epic.
 *
 * @author 
 * @version 1.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StaffRoleTests2 {

    private DatabaseHelper dbHelper;
    private User staffUser;
    private StaffPrivateMessagesPage staffPage;

    /**
     * Initializes JavaFX once before any tests run, and prevents it from shutting down
     * after each window closes so that our UI test can execute.
     */
    @BeforeAll
    public void initToolkit() {
        // Start JavaFX platform
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // already initialized
        }
        // Keep JavaFX alive after windows close
        Platform.setImplicitExit(false);
    }

    /**
     * Sets up the test environment before each test. This includes initializing the database connection,
     * clearing relevant tables, and registering a dummy staff user.
     *
     * @throws SQLException if a database access error occurs.
     */
    @BeforeEach
    public void setup() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
        clearDatabase();
        // Create and register a dummy staff user.
        staffUser = new User("staffUser", "staff@example.com", "Staff Member", "password", "staff");
        dbHelper.register(staffUser);
        dbHelper.setUserRole("staffUser", "staff", true);
        staffPage = new StaffPrivateMessagesPage(dbHelper);
    }

    /**
     * Tears down the test environment after each test.
     */
    @AfterEach
    public void teardown() {
        dbHelper.closeConnection();
    }

    /**
     * Clears the necessary database tables for testing.
     */
    private void clearDatabase() {
        try (Connection conn = DriverManager.getConnection(
                     DatabaseHelper.DB_URL,
                     DatabaseHelper.USER,
                     DatabaseHelper.PASS);
             Statement stmt = conn.createStatement()) {

            String[] tables = {
                "PRIVATE_MESSAGES",
                "ANSWERS",
                "QUESTIONS",
                "cse360users",
                "REVIEWS",
                "StudentReviewerWeights",
                "TRUSTED_REVIEWERS",
                "InvitationCodes"
            };
            for (String table : tables) {
                stmt.executeUpdate("DELETE FROM " + table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests that a staff user is able to view discussion board questions.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testStaffCanViewDiscussionQuestions() throws SQLException {
        Questions questions = new Questions(dbHelper);
        Question q = new Question("Test Question", "Tester", false);
        questions.addQuestion(q);

        Question fetched = questions.getQuestionByText("Test Question");
        assertNotNull(fetched, "Staff should be able to view discussion questions.");
    }

    /**
     * Tests that a staff user can view answers for a discussion board question.
     *
     * @throws SQLException if a database access error occurs.
     */
    @Test
    public void testStaffCanViewDiscussionAnswers() throws SQLException {
        Questions questions = new Questions(dbHelper);
        Answers answers = new Answers(dbHelper);
        Question q = new Question("Test Question", "Tester", false);
        questions.addQuestion(q);

        Answer a = new Answer(q.getText(), "Test Answer", "Answerer", false, false, false);
        answers.addAnswer(a);

        List<Answer> fetchedAnswers = answers.getAnswersByQuestionText(q.getText());
        assertFalse(fetchedAnswers.isEmpty(), "Staff should be able to view answers for a question.");
    }

    /**
     * Tests that a staff user is able to read private chat messages sent to them.
     */
    @Test
    public void testStaffCanReadPrivateChats() {
        dbHelper.savePrivateMessage("student1", "staffUser", "Hello staff!");
        List<String> messages = dbHelper.fetchPrivateMessages("staffUser", "student1");

        assertFalse(messages.isEmpty(), "Staff should be able to retrieve private chat messages.");
        assertTrue(messages.get(0).contains("Hello staff!"),
                   "The retrieved message should match expected text.");
    }

    /**
     * Tests that a staff user can send private messages.
     */
    @Test
    public void testStaffCanSendPrivateMessages() {
        dbHelper.savePrivateMessage("staffUser", "instructor1", "Message from staff.");
        List<String> messages = dbHelper.fetchPrivateMessages("staffUser", "instructor1");

        assertFalse(messages.isEmpty(), "Staff should be able to send private messages.");
        assertTrue(messages.get(0).contains("Message from staff."),
                   "The sent message should be retrievable.");
    }

    /**
     * Tests that the staff private messaging interface displays without errors.
     * This UI test runs on the JavaFX Application Thread and uses a latch to wait
     * for the window to be shown.
     *
     * @throws InterruptedException if the UI does not initialize before timeout.
     */
    @Test
    public void testStaffPrivateMessagingInterface() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Stage dummyStage = new Stage();
            staffPage.show(dummyStage, staffUser);
            latch.countDown();
        });

        assertTrue(latch.await(5, TimeUnit.SECONDS),
                   "The staff messaging interface did not display within the timeout.");
    }
}
