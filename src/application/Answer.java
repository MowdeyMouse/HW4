package application;

public class Answer {

    private String questionText;  
    private String text;          
    private String author; 
    private boolean accepted;
    private boolean isRead;
    private boolean isClarification;

    public Answer(String questionText, String text, String author, boolean accepted, boolean isRead, boolean isClarification) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer text cannot be empty");
        }
        this.questionText = questionText;
        this.text = text;
        this.author = author;
        this.accepted = accepted;
        this.isRead = isRead;
        this.isClarification = isClarification;
    }

    // GETTERS
    public String getQuestionText() {
        return questionText;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }
    
    public boolean getAccepted() {
    	return accepted;
    }
    
    public boolean getRead() {
    	return isRead;
    }
    
    public boolean isClarification() {
    	return isClarification;
    }

    // SETTER
    public void setText(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer text cannot be empty");
        }
        this.text = newText;
    }
    
    public void setAccepted(boolean accepted) {
    	this.accepted = accepted;
    }
    
    public void setRead(boolean isRead) {
    	this.isRead = isRead;
    }
    
    @Override
    public String toString() {
    	String read = "Unread";
    	if(isRead)
    		read = "Read";
    	if(isClarification) {
    		return "Requst for clarification: " + text + " (" + read + " | Asked by " + author + ")";
    	}
    	String accept = "";
    	if(accepted) {
    		accept = " [Accepted Answer]";
    	}
        return text + " (" + read + " | Answered by " + author + ")" + accept;
    }
}
