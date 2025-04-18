package application;

public class Question {

    private String text;
    private String author;
    private boolean isResolved;

    public Question(String text, String author, boolean isResolved) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        this.text = text;
        this.author = author;
        this.isResolved = isResolved;
    }

    // GETTERS
    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }
    
    public boolean getResolved() {
    	return isResolved;
    }

    // SETTER
    public void setText(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        this.text = newText;
    }
    
    public void setResolved(boolean resolveTo) {
    	this.isResolved = resolveTo; 
    }


    @Override
    public String toString() {
    	String resolved = "Unresolved | ";
    	if(this.isResolved)
    		resolved = "Resolved | ";
        return text + " (" + resolved + "Asked by " + author + ")";
    }
}
