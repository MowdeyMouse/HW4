package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import databasePart1.DatabaseHelper;

public class Reviews {

    private DatabaseHelper dbHelper;

    public Reviews(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // Create review.
    public boolean addReview(Review review) {
        try {
            dbHelper.saveReview(review);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all reviews for a question (by text)
    public List<Review> getReviewsForQuestion(String questionText) {
        try {
            return dbHelper.fetchReviewsForTarget(questionText, "QUESTION");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Get reviews for an answer (by text)
    public List<Review> getReviewsForAnswer(String answerText) {
        try {
            return dbHelper.fetchReviewsForTarget(answerText, "ANSWER");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // Get reviews for an author
    public List<Review> getReviewsByAuthor(String authorUserName) {
        try {
            return dbHelper.fetchReviewsByAuthor(authorUserName);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Update review.
    public boolean updateReview(int reviewId, String newText) {
        try {
            return dbHelper.updateReview(reviewId, newText);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete rewview by ID
    public boolean removeReview(int reviewId) {
        try {
            return dbHelper.deleteReview(reviewId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
