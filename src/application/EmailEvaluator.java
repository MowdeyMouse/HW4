package application;


public class EmailEvaluator {
	/**
	 * <p> Title: Directed Graph-translated email Assessor. </p>
	 * 
	 * <p> Description: A demonstration of the mechanical translation of Directed Graph 
	 * diagram into an executable Java program using the email Evaluator Directed Graph. 
	 * The code detailed design is based on a while loop with a cascade of if statements</p>
	 * 
	 * <p> Copyright: Lynn Robert Carter © 2022 </p>
	 * 
	 * @author Lynn Robert Carter
	 * 
	 * @version 0.00		2018-02-22	Initial baseline 
	 * 
	 */

	/**********************************************************************************************
	 * 
	 * Result attributes to be used for GUI applications where a detailed error message and a 
	 * pointer to the character of the error will enhance the user experience.
	 * 
	 */

	private static String emailErrorMessage = "";		// The error message text
	private static String emailInput = "";			// The input being processed
	private static int emailIndexofError = -1;		// The index where the error was located
	private static boolean foundUpperCaseInDomain = false;
	private static boolean foundLowerCase = false;
	private static boolean foundNumericDigitInDomain = false;
	private static boolean foundLongEnough = false;
	private static boolean foundOtherChar = false;
	private static boolean foundAtSign = false;
	private static boolean foundPeriod = false;
	private static String inputLine = "";				// The input line
	private static char currentChar;					// The current character in the line
	private static int currentCharNdx;					// The index of the current character
	private static boolean running;						// The flag that specifies if the FSM is 
														// running

	/**********
	 * This private method display the input line and then on a line under it displays an up arrow
	 * at the point where an error should one be detected.  This method is designed to be used to 
	 * display the error message on the console terminal.
	 * 
	 * @param input				The input string
	 * @param currentCharNdx	The location where an error was found
	 * @return					Two lines, the entire input line followed by a line with an up arrow
	 */
	private static void displayInputState() {
		// Display the entire input line
		System.out.println(inputLine);
		System.out.println(inputLine.substring(0,currentCharNdx) + "?");
		System.out.println("The email size: " + inputLine.length() + "  |  The currentCharNdx: " + 
				currentCharNdx + "  |  The currentChar: \"" + currentChar + "\"");
	}

	/**********
	 * This method is a mechanical transformation of a Directed Graph diagram into a Java
	 * method.
	 * 
	 * @param input		The input string for directed graph processing
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a help description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */
	public static String evaluateEmail(String input) {
		// The following are the local variable used to perform the Directed Graph simulation
		emailErrorMessage = "";
		emailIndexofError = 0;			// Initialize the IndexofError
		inputLine = input;					// Save the reference to the input line as a global
		currentCharNdx = 0;					// The index of the current character
		
		if(input.length() <= 0) return "*** Error *** The email is empty!";
		
		// The input is not empty, so we can access the first character
		currentChar = input.charAt(0);		// The current character from the above indexed position

		// The Directed Graph simulation continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state

		emailInput = input;				// Save a copy of the input
		foundUpperCaseInDomain = false;				// Reset the Boolean flag
		foundLowerCase = false;				// Reset the Boolean flag
		foundNumericDigitInDomain = false;			// Reset the Boolean flag
		foundNumericDigitInDomain = false;			// Reset the Boolean flag
		foundLongEnough = false;			// Reset the Boolean flag
		foundAtSign = false;			// Reset the Boolean flag
		foundPeriod = false;			// Reset the Boolean flag
		foundOtherChar = false;				// Reset the Boolean flag
		
		running = true;						// Start the loop

		// The Directed Graph simulation continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition
		while (running) {
			// displayInputState();
			// The cascading if statement sequentially tries the current character against all of the
			// valid transitions
			if (currentChar >= 'A' && currentChar <= 'Z') {
				if(foundAtSign) {
					foundUpperCaseInDomain = true;
				}
			} else if (currentChar >= 'a' && currentChar <= 'z') {
				foundLowerCase = true;
			} else if (currentChar >= '0' && currentChar <= '9') {
				if (foundAtSign) {
					foundNumericDigitInDomain = true;
				}
			} else if (currentChar == '.') {
				foundPeriod = true;
			} else if (currentChar == '@' && !foundAtSign) {
				foundAtSign = true;
			} else {
				foundOtherChar = true;
				running = false;
			}
			if (currentCharNdx >= 7) {
				foundLongEnough = true;
			}
			
			// Go to the next character if there is one
			currentCharNdx++;
			if (currentCharNdx >= inputLine.length())
				running = false;
			else
				currentChar = input.charAt(currentCharNdx);
		}
		
		String errMessage = "";
		if (foundOtherChar)
			errMessage += "No special chars besides a single '@' and '.'; ";
		if (!foundAtSign)
			errMessage += "Must contain a '@' ";
		if (!foundPeriod)
			errMessage += "Must contain a '.' ";
		if (foundUpperCaseInDomain)
			errMessage += "Domain must not contain uppercase.";
		if (foundNumericDigitInDomain)
			errMessage += "Domain must not contain digits.";
		
		if (errMessage == "")
			return "";
		
		emailIndexofError = currentCharNdx;
		return "Email error: " + errMessage + "conditions were not satisfied";

	}
}
