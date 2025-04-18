package application; 

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

public class ManualQuestionsTest {

    public static void main(String[] args) {
        DatabaseHelper dbHelper = new DatabaseHelper();
        try {
            // Connect to the DB
            dbHelper.connectToDatabase();

            // Create a Questions manager
            Questions questionsManager = new Questions(dbHelper);

            // TEST 1: CREATE (Positive Test)
            System.out.println("=== TEST CREATE QUESTION (Positive) ===");
            Question q1 = new Question("When is the project due?", "Alice", false);
            questionsManager.addQuestion(q1);
            Question fetched = questionsManager.getQuestionByText("When is the project due?");
            if (fetched != null && fetched.getAuthor().equals("Alice")) {
                System.out.println("PASS: Question created successfully.");
            } else {
                System.out.println("FAIL: Question not found after creation.");
            }

            // TEST 2: CREATE (Negative Test - Empty Text)
            System.out.println("=== TEST CREATE QUESTION (Negative: Empty Text) ===");
            try {
                Question qInvalid = new Question("", "Bob", false);
                questionsManager.addQuestion(qInvalid);
                System.out.println("FAIL: Question with empty text created.");
            } catch (IllegalArgumentException ex) {
                System.out.println("PASS: Question with empty text cannot be created.");
            }


            // TEST 3: UPDATE QUESTION (Positive)
            System.out.println("=== TEST UPDATE QUESTION (Positive) ===");
            boolean updated = questionsManager.updateQuestion("When is the project due?",
                                                                "When is the final project due?");
            if (updated) {
                Question updatedQ = questionsManager.getQuestionByText("When is the final project due?");
                if (updatedQ != null && updatedQ.getAuthor().equals("Alice")) {
                    System.out.println("PASS: Question text updated successfully.");
                } else {
                    System.out.println("FAIL: Updated question not found by new text.");
                }
            } else {
                System.out.println("FAIL: updateQuestion returned false.");
            }
            
       


            // TEST 5: SEARCH QUESTIONS (Positive)
            System.out.println("=== TEST SEARCH QUESTIONS (Positive) ===");
            // Add additional realistic questions (e.g., from an Ed Discussion scenario)
            questionsManager.addQuestion(new Question("When will the exam be held?", "Bob", false));
            questionsManager.addQuestion(new Question("What topics are covered in the exam?", "Carol", false));
            questionsManager.addQuestion(new Question("Is there a review session before the exam?", "Dave", false));

            List<Question> foundExam = questionsManager.searchQuestions("exam");
            if (foundExam.size() >= 2) {
                System.out.println("PASS: Found " + foundExam.size() + " question(s) containing 'exam'.");
            } else {
                System.out.println("FAIL: Expected to find multiple questions containing 'exam'.");
            }

            // TEST 6: READ ALL QUESTIONS (List Test)
            System.out.println("=== TEST READ ALL QUESTIONS ===");
            List<Question> allQs = questionsManager.getAllQuestions();
            if (!allQs.isEmpty()) {
                System.out.println("PASS: Retrieved " + allQs.size() + " question(s) from the DB.");
            } else {
                System.out.println("FAIL: No questions found.");
            }
            
            // TEST 7: SEARCH QUESTIONS (Negative)
            System.out.println("=== TEST SEARCH QUESTIONS (Negative) ===");
            List<Question> foundNone = questionsManager.searchQuestions("nonexistentkeyword");
            if (foundNone.isEmpty()) {
                System.out.println("PASS: Search for 'nonexistentkeyword' returned no results as expected.");
            } else {
                System.out.println("FAIL: Search for 'nonexistentkeyword' should return empty.");
            }

            // TEST 8: DELETE QUESTION (Positive)
            System.out.println("=== TEST DELETE QUESTION (Positive) ===");
            boolean deleted = questionsManager.removeQuestion("When is the final project due?");
            if (deleted) {
                Question verifyGone = questionsManager.getQuestionByText("When is the final project due?");
                if (verifyGone == null) {
                    System.out.println("PASS: Question removed successfully.");
                } else {
                    System.out.println("FAIL: Question still exists after deletion.");
                }
            } else {
                System.out.println("FAIL: removeQuestion returned false.");
            }



        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbHelper.closeConnection();
        }
    }
}
