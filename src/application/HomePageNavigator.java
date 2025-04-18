package application;

import databasePart1.DatabaseHelper;
import javafx.stage.Stage;

public class HomePageNavigator {
    /**
     * Navigate the user to the appropriate page based on the given role.
     */
    public static void navigateToRoleHome(DatabaseHelper databaseHelper, Stage primaryStage, User user, String role) {
        switch (role) {
            case "admin":
            	user.setRole("admin");
                new AdminHomePage(databaseHelper).show(primaryStage, user);
                break;
            case "student":
            	user.setRole("student");
                new UserHomePage(databaseHelper).show(primaryStage, user);
                break;
            case "instructor":
            	user.setRole("instructor");
                new InstructorHomePage(databaseHelper).show(primaryStage, user);
                break;
            case "staff":
            	user.setRole("staff");
                new StaffHomePage(databaseHelper).show(primaryStage, user);
                break;
            case "reviewer":
            	user.setRole("reviewer");
                new ReviewerHomePage(databaseHelper).show(primaryStage, user);
                break;
            default:
                System.err.println("Unknown role: " + role);
        }
    }
}
