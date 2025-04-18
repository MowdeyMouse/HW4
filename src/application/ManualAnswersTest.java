package application; 

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

public class ManualAnswersTest {

    public static void main(String[] args) {
        DatabaseHelper dbHelper = new DatabaseHelper();
        try {
            // Connect to the DB
            dbHelper.connectToDatabase();

            // Create managers for Questions and Answers
            Questions questionsManager = new Questions(dbHelper);
            Answers answersManager = new Answers(dbHelper);

            // SETUP: Create a Question for Answer tests
            Question setupQ = new Question("What is the answer to everything?", "Tester", false);
            questionsManager.addQuestion(setupQ);
            Question fetchedQ = questionsManager.getQuestionByText("What is the answer to everything?");
            if (fetchedQ != null) {
                System.out.println("PASS: Setup question created successfully.");
            } else {
                System.out.println("FAIL: Setup question not created.");
            }

            // TEST 1: CREATE ANSWER (Positive Test)
            System.out.println("=== TEST CREATE ANSWER (Positive) ===");
            Answer a1 = new Answer(setupQ.getText(), "42", "Alice", false, false, false);
            answersManager.addAnswer(a1);
            List<Answer> fetchedAnswers = answersManager.getAnswersByQuestionText(setupQ.getText());
            boolean found = false;
            for (Answer a : fetchedAnswers) {
                if (a.getText().equals("42") && a.getAuthor().equals("Alice")) {
                    found = true;
                    break;
                }
            }
            if (found) {
                System.out.println("PASS: Answer created successfully.");
            } else {
                System.out.println("FAIL: Answer not found after creation.");
            }

            // TEST 2: CREATE ANSWER (Negative Test - Empty Text)
            System.out.println("=== TEST CREATE ANSWER (Negative: Empty Text) ===");
            try {
                Answer aInvalid = new Answer(setupQ.getText(), "", "Bob", false, false, false);
                answersManager.addAnswer(aInvalid);
                System.out.println("FAIL: Answer with empty text created.");
            } catch (IllegalArgumentException ex) {
                System.out.println("PASS: Answer with empty text cannot be created.");
            }

            // TEST 3: UPDATE ANSWER (Positive)
            System.out.println("=== TEST UPDATE ANSWER (Positive) ===");
            boolean updated = answersManager.updateAnswer("42", "Forty Two");
            if (updated) {
                List<Answer> updatedAnswers = answersManager.getAnswersByQuestionText(setupQ.getText());
                boolean foundUpdated = false;
                for (Answer a : updatedAnswers) {
                    if (a.getText().equals("Forty Two") && a.getAuthor().equals("Alice")) {
                        foundUpdated = true;
                        break;
                    }
                }
                if (foundUpdated) {
                    System.out.println("PASS: Answer updated successfully.");
                } else {
                    System.out.println("FAIL: Updated answer not found.");
                }
            } else {
                System.out.println("FAIL: updateAnswer returned false.");
            }

            // TEST 4: READ ALL ANSWERS (List Test)
            System.out.println("=== TEST READ ALL ANSWERS ===");
            List<Answer> allAnswers = answersManager.getAllAnswers();
            if (!allAnswers.isEmpty()) {
                System.out.println("PASS: Retrieved " + allAnswers.size() + " answer(s) from the DB.");
            } else {
                System.out.println("FAIL: No answers found.");
            }

            // TEST 5: DELETE ANSWER (Positive)
            System.out.println("=== TEST DELETE ANSWER (Positive) ===");
            boolean deleted = answersManager.removeAnswer("Forty Two");
            if (deleted) {
                List<Answer> answersAfterDelete = answersManager.getAnswersByQuestionText(setupQ.getText());
                boolean stillExists = false;
                for (Answer a : answersAfterDelete) {
                    if (a.getText().equals("Forty Two")) {
                        stillExists = true;
                        break;
                    }
                }
                if (!stillExists) {
                    System.out.println("PASS: Answer removed successfully.");
                } else {
                    System.out.println("FAIL: Answer still exists after deletion.");
                }
            } else {
                System.out.println("FAIL: removeAnswer returned false.");
            }

            // TEST 6: DELETE ANSWER (Negative: Non-existent)
            System.out.println("=== TEST DELETE ANSWER (Negative: Non-existent) ===");
            boolean deleteNonExist = answersManager.removeAnswer("Non-existent Answer Text");
            if (!deleteNonExist) {
                System.out.println("PASS: Deleting non-existent answer returned false as expected.");
            } else {
                System.out.println("FAIL: Deleting non-existent answer should not succeed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dbHelper.closeConnection();
        }
    }
}
