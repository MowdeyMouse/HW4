package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import databasePart1.DatabaseHelper;

public class Questions {

    private DatabaseHelper dbHelper;

    public Questions(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void addQuestion(Question question) {
        try {
            dbHelper.saveQuestion(question.getText(), question.getAuthor());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Question> getAllQuestions() {
        try {
            return dbHelper.fetchAllQuestions();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<Question> getUnresolvedQuestions() {
    	try {
            return dbHelper.fetchUnresolvedQuestions();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Question getQuestionByText(String text) {
        try {
            return dbHelper.fetchQuestionByText(text);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Updates selected question

    public boolean updateQuestion(String oldText, String newText) {
        try {
            return dbHelper.updateQuestion(oldText, newText);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean setResolveQuestion(String text, boolean resolved) {
    	try {
    		return dbHelper.UpdateResolved(text, resolved);
    	} catch (SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    }

    // Deletes selected question
    public boolean removeQuestion(String text) {
        try {
            return dbHelper.deleteQuestion(text);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Returns a list of questions whose text contains the keyword

    public List<Question> searchQuestions(String keyword) {
        try {
            return dbHelper.searchQuestionsByKeyword(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
