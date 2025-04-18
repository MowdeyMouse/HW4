package application;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
import java.util.ArrayList;
import java.util.List;

public class User {
    private String userName;
    private String email;
    private String name;
    private String password;
    private String role;

    // ADDED FOR TRUSTED REVIEWERS
    private List<String> trustedReviewers = new ArrayList<>();

    // Constructor to initialize a new User object with userName, password, and role.
    public User(String userName, String email, String name, String password, String role) {
        this.userName = userName;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role;
    }
    
    // Sets the role of the user.
    public void setRole(String role) {
    	this.role=role;
    }

    public String getUserName() { return userName; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // ADDED FOR TRUSTED REVIEWERS:
    public List<String> getTrustedReviewers() {
        return trustedReviewers;
    }
    public void setTrustedReviewers(List<String> reviewerList) {
        this.trustedReviewers = reviewerList;
    }
    public void addTrustedReviewer(String reviewerUserName) {
        if (!trustedReviewers.contains(reviewerUserName)) {
            trustedReviewers.add(reviewerUserName);
        }
    }
    public void removeTrustedReviewer(String reviewerUserName) {
        trustedReviewers.remove(reviewerUserName);
    }
    public boolean isTrustedReviewer(String reviewerUserName) {
        return trustedReviewers.contains(reviewerUserName);
    }
}
