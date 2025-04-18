package application;

import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import databasePart1.DatabaseHelper;

public class Answers {

    private DatabaseHelper dbHelper;

    public Answers(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }


     // Adds a new Answer (in the DB).
    public void addAnswer(Answer answer) {
        try {
            dbHelper.saveAnswer(
                answer.getQuestionText(),
                answer.getText(),
                answer.getAuthor(),
                answer.isClarification()
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Returns all answers from the database
     
    public List<Answer> getAllAnswers() {
        try {
            return dbHelper.fetchAllAnswers();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Returns all answers for a given question
    
    public List<Answer> getAnswersByQuestionText(String questionText) {
        try {
            return dbHelper.fetchAnswersByQuestionText(questionText);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Answer> getAcceptedAnswersByQuestionText(String questionText) {
        try {
            return dbHelper.fetchAcceptedAnswersByQuestionText(questionText);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Answer> getUnreadAnswersByQuestionText(String questionText) {
        try {
            return dbHelper.fetchUnreadAnswersByQuestionText(questionText);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // updates answer
    public boolean updateAnswer(String oldText, String newText) {
        try {
            return dbHelper.updateAnswer(oldText, newText);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean acceptAnswer(String text, boolean accepted) {
    	try {
            return dbHelper.acceptAnswer(text, accepted);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean readAnswer(String text, boolean read) {
    	try {
            return dbHelper.readAnswer(text, read);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // deletes selected answer
    public boolean removeAnswer(String text) {
        try {
            return dbHelper.deleteAnswer(text);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
