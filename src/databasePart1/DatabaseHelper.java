package databasePart1;
import java.sql.*;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import application.Answer;
import application.Question;
import application.Review;
import application.User;

import java.time.*;
import java.time.format.*;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

    // JDBC driver name and database URL 
    
	// DatabaseHelper.java
	public static final String JDBC_DRIVER = "org.h2.Driver";
	public static final String DB_URL      = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	public static final String USER        = "sa";
	public static final String PASS        = "";



    private Connection connection = null;
    private Statement statement = null; 
    //	PreparedStatement pstmt

    public void connectToDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER); // Load the JDBC driver
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement(); 
            // You can use this command to clear the database and restart from fresh.
            // statement.execute("DROP ALL OBJECTS");

            createTables();  // Create the necessary tables if they don't exist
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }
    private void ensureAcceptedColumnExists() throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, "ANSWERS", "ACCEPTED")) {
            if (!rs.next()) {
                // Column 'accepted' does not exist. Add it.
                String alterSql = "ALTER TABLE ANSWERS ADD COLUMN accepted BOOLEAN DEFAULT FALSE";
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute(alterSql);
                }
            }
        }
    }


    private void createTables() throws SQLException {
        // cse360users table
        String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "email VARCHAR(255), "
                + "name VARCHAR(255), "
                + "userName VARCHAR(255) UNIQUE, "
                + "password VARCHAR(255), "
                + "role VARCHAR(20), "
                + "oneTime VARCHAR(20),"
                + "isAdmin BOOLEAN, "
                + "isStudent BOOLEAN, "
                + "isInstructor BOOLEAN, "
                + "isStaff BOOLEAN, "
                + "isReviewer BOOLEAN"
                + ")";
        statement.execute(userTable);

        // Invitation codes
        String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
                + "code VARCHAR(10) PRIMARY KEY, "
                + "isUsed BOOLEAN DEFAULT FALSE, "
                + "expireTime TIMESTAMP)";
        statement.execute(invitationCodesTable);

        // QUESTIONS table
        String questionsTable = "CREATE TABLE IF NOT EXISTS QUESTIONS ("
                + "text VARCHAR(255) PRIMARY KEY, "
                + "author VARCHAR(255), "
                + "isResolved BOOLEAN, "
                + "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        statement.execute(questionsTable);

        // ANSWERS table
        String answersTable = "CREATE TABLE IF NOT EXISTS ANSWERS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "questionText VARCHAR(255), "
                + "answerText VARCHAR(255), "
                + "author VARCHAR(255), "
                + "isClarification BOOLEAN DEFAULT FALSE, "
                + "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "isRead BOOLEAN, "
                + "accepted BOOLEAN DEFAULT FALSE"
                + ")";
        statement.execute(answersTable);

        // REVIEWS table
        String reviewsTable = "CREATE TABLE IF NOT EXISTS REVIEWS ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "targetId VARCHAR(255) NOT NULL, "
                + "targetType VARCHAR(10) NOT NULL, "
                + "reviewText VARCHAR(1000), "
                + "author VARCHAR(255))";
        statement.execute(reviewsTable);
        
        // WEIGHTS table
        String weightsTable = "CREATE TABLE IF NOT EXISTS StudentReviewerWeights ("
                + "studentUserName VARCHAR(255) NOT NULL, "
                + "reviewerUserName VARCHAR(255) NOT NULL, "
                + "weight INT NOT NULL CHECK (weight >= 1 AND weight <= 5), "
                + "PRIMARY KEY (studentUserName, reviewerUserName), "
                + "FOREIGN KEY (studentUserName) REFERENCES cse360users(userName) ON DELETE CASCADE, "
                + "FOREIGN KEY (reviewerUserName) REFERENCES cse360users(userName) ON DELETE CASCADE)";
        statement.execute(weightsTable);

        // ADDED FOR TRUSTED REVIEWERS
        String trustedReviewersTable = "CREATE TABLE IF NOT EXISTS TRUSTED_REVIEWERS ("
                + "studentUser VARCHAR(255), "
                + "reviewerUser VARCHAR(255))";
        statement.execute(trustedReviewersTable);
        
        // ADDED FOR REQUESTING REVIEWER ROLE
        String reviewerRequests = "CREATE TABLE IF NOT EXISTS REQUESTED_REVIEWERS ("
        		+ "userName VARCHAR(255))";
        statement.execute(reviewerRequests);

        // ADDED FOR PRIVATE MESSAGING
        String privateMessagesTable = "CREATE TABLE IF NOT EXISTS PRIVATE_MESSAGES ("
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "sender VARCHAR(255), "
                + "recipient VARCHAR(255), "
                + "messageText VARCHAR(1000), "
                + "sentTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        statement.execute(privateMessagesTable);
    }

    // For debugging
    public void printAllTablesAndContents() throws SQLException {
        String getTablesQuery = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet tables = stmt.executeQuery(getTablesQuery)) {

            while (tables.next()) {
                String tableName = tables.getString(1);
                System.out.println("Table: " + tableName);
                printTableContents(tableName);
            }
        }
    }

    private void printTableContents(String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column headers
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // Print rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        }
    }

    public void saveQuestion(String text, String author) throws SQLException {
        String sql = "INSERT INTO QUESTIONS (text, author, isResolved) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, text);
            pstmt.setString(2, author);
            pstmt.setBoolean(3, false);
            pstmt.executeUpdate();
        }
    }

    public List<Question> fetchAllQuestions() throws SQLException {
        List<Question> result = new ArrayList<>();
        String sql = "SELECT text, author, isResolved FROM QUESTIONS";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String text = rs.getString("text");
                String author = rs.getString("author");
                boolean isResolved = rs.getBoolean("isResolved");
                result.add(new Question(text, author, isResolved));
            }
        }
        return result;
    }
    
    public List<Question> fetchUnresolvedQuestions() throws SQLException {
    	List<Question> result = new ArrayList<>();
        String sql = "SELECT text, author, isResolved FROM QUESTIONS";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
            	if(!rs.getBoolean("isResolved")) {
            		String text = rs.getString("text");
            		String author = rs.getString("author");
            		boolean isResolved = rs.getBoolean("isResolved");
            		result.add(new Question(text, author, isResolved));
            	}
            }
        }
        return result;
    }

    public boolean updateQuestion(String oldText, String newText) throws SQLException {
        String sql = "UPDATE QUESTIONS SET text = ? WHERE text = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newText);
            pstmt.setString(2, oldText);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean UpdateResolved(String text, boolean resolved) throws SQLException {
    	String update = "UPDATE QUESTIONS SET isResolved = ? WHERE text = ?";
    	try (PreparedStatement pstmt = connection.prepareStatement(update)) {
            pstmt.setBoolean(1, resolved);
            pstmt.setString(2, text);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteQuestion(String text) throws SQLException {
        String sql = "DELETE FROM QUESTIONS WHERE text = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, text);
            return pstmt.executeUpdate() > 0;
        }
    }

    public Question fetchQuestionByText(String text) throws SQLException {
        String sql = "SELECT author, isResolved FROM QUESTIONS WHERE text = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, text);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String author = rs.getString("author");
                    boolean isResolved = rs.getBoolean("isResolved");
                    return new Question(text, author, isResolved);
                }
            }
        }
        return null;
    }


    // Searches for questions containing the given keyword
    public List<Question> searchQuestionsByKeyword(String keyword) throws SQLException {
        List<Question> result = new ArrayList<>();
        String sql = "SELECT text, author, isResolved FROM QUESTIONS WHERE LOWER(text) LIKE LOWER(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String text = rs.getString("text");
                    String author = rs.getString("author");
                    boolean isResolved = rs.getBoolean("isResolved");
                    result.add(new Question(text, author, isResolved));
                }
            }
        }
        return result;
    }

    // Answers
    public void saveAnswer(String questionText, String text, String author, boolean isClarification) throws SQLException {
        ensureAcceptedColumnExists();  // Ensure the column exists before inserting.
        String sql = "INSERT INTO ANSWERS (questionText, answerText, author, accepted, isRead, isClarification) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, questionText);
            pstmt.setString(2, text);
            pstmt.setString(3, author);
            pstmt.setBoolean(4, false);
            pstmt.setBoolean(5, false);
            pstmt.setBoolean(6, isClarification);
            pstmt.executeUpdate();
        }
    }


    public List<Answer> fetchAllAnswers() throws SQLException {
        List<Answer> result = new ArrayList<>();
        String sql = "SELECT questionText, answerText, author, accepted, isRead, isClarification FROM ANSWERS";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String qText = rs.getString("questionText");
                String aText = rs.getString("answerText");
                String author = rs.getString("author");
                boolean accepted = rs.getBoolean("accepted");
                boolean isRead = rs.getBoolean("isRead");
                boolean isClarification = rs.getBoolean("isClarification");
                result.add(new Answer(qText, aText, author, accepted, isRead, isClarification));
            }
        }
        return result;
    }

    public List<Answer> fetchAnswersByQuestionText(String questionText) throws SQLException {
        ensureAcceptedColumnExists();  // Ensure the column exists before querying.
        List<Answer> result = new ArrayList<>();
        String sql = "SELECT answerText, author, accepted, isRead, isClarification FROM ANSWERS WHERE questionText = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, questionText);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String aText = rs.getString("answerText");
                    String author = rs.getString("author");
                    boolean accepted = rs.getBoolean("accepted");
                    boolean isRead = rs.getBoolean("isRead");
                    boolean isClarification = rs.getBoolean("isClarification");
                    result.add(new Answer(questionText, aText, author, accepted, isRead, isClarification));
                }
            }
        }
        return result;
    }

    
    
    public List<Answer> fetchAcceptedAnswersByQuestionText(String questionText) throws SQLException {
        List<Answer> result = new ArrayList<>();
        String sql = "SELECT answerText, author, accepted, isRead, isClarification FROM ANSWERS WHERE questionText = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, questionText);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                	if(rs.getBoolean("accepted")) {
                		String aText = rs.getString("answerText");
                    	String author = rs.getString("author");
                    	boolean accepted = rs.getBoolean("accepted");
                    	boolean isRead = rs.getBoolean("isRead");
                    	boolean isClarification = rs.getBoolean("isClarification");
                    	result.add(new Answer(questionText, aText, author, accepted, isRead, isClarification));
                	}
                }
            }
        }
        return result;
    }
    
    public List<Answer> fetchUnreadAnswersByQuestionText(String questionText) throws SQLException {
        List<Answer> result = new ArrayList<>();
        String sql = "SELECT answerText, author, accepted, isRead, isClarification FROM ANSWERS WHERE questionText = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, questionText);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                	if(!rs.getBoolean("isRead")) {
                		String aText = rs.getString("answerText");
                    	String author = rs.getString("author");
                    	boolean accepted = rs.getBoolean("accepted");
                    	boolean isRead = rs.getBoolean("isRead");
                    	boolean isClarification = rs.getBoolean("isClarification");
                    	result.add(new Answer(questionText, aText, author, accepted, isRead, isClarification));
                	}
                }
            }
        }
        return result;
    }

    public boolean updateAnswer(String oldText, String newText) throws SQLException {
        String sql = "UPDATE ANSWERS SET answerText = ? WHERE answerText = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newText);
            pstmt.setString(2, oldText);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean acceptAnswer(String text, boolean accepted) throws SQLException {
        String sql = "UPDATE ANSWERS SET accepted = ? WHERE answerText = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, accepted);
            pstmt.setString(2, text);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean readAnswer(String text, boolean read) throws SQLException {
        String sql = "UPDATE ANSWERS SET isRead = ? WHERE answerText = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, read);
            pstmt.setString(2, text);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteAnswer(String text) throws SQLException {
        String sql = "DELETE FROM ANSWERS WHERE answerText = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, text);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public void saveReview(Review review) throws SQLException {
        String sql = "INSERT INTO REVIEWS (targetId, targetType, reviewText, author) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, review.getTargetId());
            pstmt.setString(2, review.getTargetType());
            pstmt.setString(3, review.getText());
            pstmt.setString(4, review.getAuthor());
            pstmt.executeUpdate();
        }
    }
    
    public List<Review> fetchReviewsForTarget(String targetId, String targetType) throws SQLException {
    	List<Review> reviews = new ArrayList<>();
        String sql = "SELECT id, reviewText, author FROM REVIEWS WHERE targetId = ? AND targetType = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, targetId);
            pstmt.setString(2, targetType);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String text = rs.getString("reviewText");
                    String author = rs.getString("author");
                    reviews.add(new Review(id, targetId, targetType, text, author));
                }
            }
        }
        
        for (Review review : reviews) {
            int totalWeight = getTotalWeightForReviewer(review.getAuthor());
        }

        Collections.sort(reviews, new Comparator<Review>() {
            public int compare(Review r1, Review r2) {
                int weight1 = getTotalWeightForReviewer(r1.getAuthor());
                int weight2 = getTotalWeightForReviewer(r2.getAuthor());

                return Integer.compare(weight2, weight1);
            }
        });

        return reviews;
    }
    
    public List<Review> fetchReviewsByAuthor(String author) throws SQLException {
        List<Review> result = new ArrayList<>();
        String sql = "SELECT id, targetId, targetType, reviewText, author FROM REVIEWS WHERE author = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, author);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String targetId = rs.getString("targetId");
                    String targetType = rs.getString("targetType");
                    String text = rs.getString("reviewText");
                    result.add(new Review(id, targetId, targetType, text, author));
                }
            }
        }
        return result;
    }
    
    public boolean updateReview(int reviewId, String newText) throws SQLException {
        String sql = "UPDATE REVIEWS SET reviewText = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, reviewId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteReview(int reviewId) throws SQLException {
        String sql = "DELETE FROM REVIEWS WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, reviewId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean setReviewerWeight(String studentUserName, String reviewerUserName, int weight) {
        String sql = "MERGE INTO StudentReviewerWeights (studentUserName, reviewerUserName, weight) KEY(studentUserName, reviewerUserName) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentUserName);
            pstmt.setString(2, reviewerUserName);
            pstmt.setInt(3, weight);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getReviewerWeight(String studentUserName, String reviewerUserName) {
        String sql = "SELECT weight FROM StudentReviewerWeights WHERE studentUserName = ? AND reviewerUserName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentUserName);
            pstmt.setString(2, reviewerUserName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("weight");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public int getTotalWeightForReviewer(String reviewerUserName) {
        String sql = "SELECT SUM(weight) AS totalWeight FROM StudentReviewerWeights WHERE reviewerUserName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, reviewerUserName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("totalWeight");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    // ADDED FOR TRUSTED REVIEWERS:
    public void saveTrustedReviewer(String student, String reviewer) {
        String sql = "INSERT INTO TRUSTED_REVIEWERS (studentUser, reviewerUser) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student);
            pstmt.setString(2, reviewer);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeTrustedReviewer(String student, String reviewer) {
        String sql = "DELETE FROM TRUSTED_REVIEWERS WHERE studentUser = ? AND reviewerUser = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student);
            pstmt.setString(2, reviewer);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> fetchTrustedReviewers(String student) {
        List<String> result = new ArrayList<>();
        String sql = "SELECT reviewerUser FROM TRUSTED_REVIEWERS WHERE studentUser = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getString("reviewerUser"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // ADDED FOR PRIVATE MESSAGING:
    public void savePrivateMessage(String sender, String recipient, String text) {
        String sql = "INSERT INTO PRIVATE_MESSAGES (sender, recipient, messageText) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sender);
            pstmt.setString(2, recipient);
            pstmt.setString(3, text);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fetch messages between 'userA' and 'userB' in chronological order
    public List<String> fetchPrivateMessages(String userA, String userB) {
        List<String> messages = new ArrayList<>();
        String sql = "SELECT sender, messageText, sentTime FROM PRIVATE_MESSAGES "
                   + "WHERE (sender = ? AND recipient = ?) OR (sender = ? AND recipient = ?) "
                   + "ORDER BY sentTime ASC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userA);
            pstmt.setString(2, userB);
            pstmt.setString(3, userB);
            pstmt.setString(4, userA);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String text = rs.getString("messageText");
                    LocalDateTime time = rs.getTimestamp("sentTime").toLocalDateTime();

                    // FORMATTED TIME/DATE:
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
                    String formattedTime = time.format(dtf);

                    // Now each chat line is "Name (MM/dd/yyyy HH:mm): message text"
                    messages.add(sender + " (" + formattedTime + "): " + text);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }


    // As a reviewer, see which students have chatted with me
    public List<String> fetchChatPartners(String reviewer) {
        List<String> partners = new ArrayList<>();
        String sql = "SELECT DISTINCT sender FROM PRIVATE_MESSAGES WHERE recipient = ? "
                   + "UNION "
                   + "SELECT DISTINCT recipient FROM PRIVATE_MESSAGES WHERE sender = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, reviewer);
            pstmt.setString(2, reviewer);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    partners.add(rs.getString("sender"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return partners;
    }

    // Check if the database is empty
    public boolean isDatabaseEmpty() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM cse360users";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt("count") == 0;
        }
        return true;
    }

    // Registers a new user in the database.
    public void register(User user) throws SQLException {
        String insertUser = "INSERT INTO cse360users (userName, email, name, password, role, oneTime, isAdmin, isStudent, isInstructor, isStaff, isReviewer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
            pstmt.setString(1, user.getUserName());        
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole());
            pstmt.setString(6, "0");
            pstmt.setBoolean(7, false);
            pstmt.setBoolean(8, false);
            pstmt.setBoolean(9, false);
            pstmt.setBoolean(10, false);
            pstmt.setBoolean(11, false);
            pstmt.executeUpdate();
        }
    }
    
    // sets the password and one-time password status given a userName.
    public void setPassword(String userName, String password, int oneTime) throws SQLException {
        String setPass = "UPDATE cse360users SET password = ?, oneTime = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(setPass)) {
            pstmt.setString(1, password);
            pstmt.setString(2, Integer.toString(oneTime));
            pstmt.setString(3, userName);
            pstmt.executeUpdate();
        }
    }
    
    public boolean isOneTime(String userName) throws SQLException {
        String query = "SELECT oneTime FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "1".equals(rs.getString("oneTime"));
                }
            }
        }
        return false;
    }

    // Deletes a user from the database.
    public void deleteUser(String userName) throws SQLException {
        String removeUser = "DELETE FROM cse360users WHERE userName= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(removeUser)) {
            pstmt.setString(1, userName);
            pstmt.executeUpdate();
        }
    }

    // Validates a user's login credentials.
    public boolean login(User user) throws SQLException {
        String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    // Checks whether a user has a role.
    public boolean hasRole(String userName, String role) {
        String column;
        switch (role.toLowerCase()) {
            case "admin":
                column = "isAdmin";
                break;
            case "student":
                column = "isStudent";
                break;
            case "instructor":
                column = "isInstructor";
                break;
            case "staff":
                column = "isStaff";
                break;
            case "reviewer":
                column = "isReviewer";
                break;
            default:
                return false;
        }
        
        String query = "SELECT " + column + " FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(column);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // If user doesn't exist
    }
    
    // Checks if a user already exists in the database based on their userName.
    public boolean doesUserExist(String userName) {
        String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // If the count is greater than 0, the user exists
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // If an error occurs, assume user doesn't exist
    }
    
    // Retrieves the user from the database using their userName.
    public User getUser(String userName) {
        String query = "SELECT * FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User(rs.getString("userName"), rs.getString("email"), rs.getString("name"), rs.getString("password"), "");  // Return the user if user exists
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // If no user exists or an error occurs
    }
    
    // Retrieves all users from the database
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM cse360users";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                User user = new User(rs.getString("userName"), rs.getString("email"), rs.getString("name"), rs.getString("password"), "");  // Return the user if user exists
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    // Retrieves the role of a user from the database using their UserName.
    public String getCurrentRole(String userName) {
        String query = "SELECT role FROM cse360users WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("role"); // Return the role if user exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // If no user exists or an error occurs
    }
    
    // Retrieves all allowed roles of a user from the database using their UserName.
    public ArrayList<String> getUserRoles(String userName) {
        String query = "SELECT isAdmin, isStudent, isInstructor, isStaff, isReviewer FROM cse360users WHERE userName = ?";
        ArrayList<String> roles = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    if (rs.getBoolean("isAdmin")) {
                        roles.add("admin");
                    }
                    if (rs.getBoolean("isStudent")) {
                        roles.add("student");
                    }
                    if (rs.getBoolean("isInstructor")) {
                        roles.add("instructor");
                    }
                    if (rs.getBoolean("isStaff")) {
                        roles.add("staff");
                    }
                    if (rs.getBoolean("isReviewer")) {
                        roles.add("reviewer");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }
    
    // Changes a user's current role.
    public void switchRole(String userName, String newRole) {
        String query = "UPDATE cse360users SET role = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newRole);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Sets whether a user can log into a role
    public void setUserRole(String userName, String role, boolean value) {
        String column;
        switch (role.toLowerCase()) {
            case "admin":
                column = "isAdmin";
                break;
            case "student":
                column = "isStudent";
                break;
            case "instructor":
                column = "isInstructor";
                break;
            case "staff":
                column = "isStaff";
                break;
            case "reviewer":
                column = "isReviewer";
                break;
            default:
                return;
        }
        
        String query = "UPDATE cse360users SET " + column + " = ? WHERE userName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBoolean(1, value);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // add a student to the requested reviewers list.
    public void addReviewerRequest(String userName) throws SQLException {
    	String sql = "INSERT INTO REQUESTED_REVIEWERS (userName) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            pstmt.executeUpdate();
        }
    }
    
    // get a list of all requested reviewers
    public ArrayList<String> getReviewerRequests() throws SQLException {
    	ArrayList<String> a = new ArrayList<String>();
    	String query = "SELECT * FROM REQUESTED_REVIEWERS";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                a.add(rs.getString("userName"));
            }
        }
    	return a;
    }
    
    public ArrayList<String> getUserContributions(String userName) throws SQLException {
    	ArrayList<String> a = new ArrayList<String>();
    	String query = "SELECT * FROM QUESTIONS WHERE author = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        	pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                a.add(rs.getString("text"));
            }
        }
        String ansquery = "SELECT * FROM ANSWERS WHERE author = ?";
        
        try (PreparedStatement pstmt2 = connection.prepareStatement(ansquery)) {
        	pstmt2.setString(1, userName);
            ResultSet rs2 = pstmt2.executeQuery();
            while (rs2.next()) {
                a.add(rs2.getString("answerText"));
            }
        }
    	return a;
    }
    
    // Generates a new invitation code and inserts it into the database.
    public String generateInvitationCode() {
        String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
        String query = "INSERT INTO InvitationCodes (code, expireTime) VALUES (?,?)";
        
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(2);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timer = expireTime.format(format);

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.setString(2, timer);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return code + "\nValid until : " + timer;
    }
    
    // Validates an invitation code to check if it is unused.
    public boolean validateInvitationCode(String code) {
        String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime expireTime = LocalDateTime.parse(rs.getString("expireTime"), format);
                if(LocalDateTime.now().isBefore(expireTime)) {
                    // Mark the code as used
                    markInvitationCodeAsUsed(code);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Marks the invitation code as used in the database.
    private void markInvitationCodeAsUsed(String code) {
        String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Closes the database connection and statement.
    public void closeConnection() {
        try{ 
            if(statement!=null) statement.close(); 
        } catch(SQLException se2) { 
            se2.printStackTrace();
        } 
        try { 
            if(connection!=null) connection.close(); 
        } catch(SQLException se){ 
            se.printStackTrace(); 
        } 
    }
}
