package validationTests;

import application.*;

/*******
 * <p> Title: EmailEvaluatorTester Class. </p>
 * 
 * <p> Description: A Java demonstration for semi-automated tests </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2022 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00	2022-02-25 A set of semi-automated test cases
 * @version 2.00	2024-09-22 Updated for use at ASU
 * 
 */
public class EmailEvaluatorTester {
	
	static int numPassed = 0;	// Counter of the number of passed tests
	static int numFailed = 0;	// Counter of the number of failed tests

	/*
	 * This mainline displays a header to the console, performs a sequence of
	 * test cases, and then displays a footer with a summary of the results
	 */
	public static void main(String[] args) {
		/************** Test cases semi-automation report header **************/
		System.out.println("______________________________________");
		System.out.println("\nTesting Automation");

		/************** Start of the Email test cases **************/
		
		//False test cases
		performTestCase(1, "oneat@twoat@domain.com", false);      // Only 1 @ symbol
		performTestCase(2, "noat.com", false);                    // Only 1 @ symbol
		performTestCase(3, "numbers@in.d0main", false);           // No numbers in domain
		performTestCase(4, "caps@IN.DOMAIN", false);              // No caps in domain
		performTestCase(5, "no@dot", false);                      // . required
		performTestCase(6, "no@iñvalid.chars", false);            // No invalid chars
		
		//True test cases
		performTestCase(7, "username@domain.com", true);          // Generic email
		performTestCase(8, "name@subdomain.domain.com", true);    // Tests subdomains
		performTestCase(9, "UPPERd1g1t@domain.com", true);        // Tests Uppercase and digit in email name
		
		
		/************** End of the test cases **************/
		
		/************** Test cases semi-automation report footer **************/
		System.out.println("____________________________________________________________________________");
		System.out.println();
		System.out.println("Number of tests passed: "+ numPassed);
		System.out.println("Number of tests failed: "+ numFailed);
	}
	
	/*
	 * This method sets up the input value for the test from the input parameters,
	 * displays test execution information, invokes precisely the same recognizer
	 * that the interactive JavaFX mainline uses, interprets the returned value,
	 * and displays the interpreted result.
	 */
	private static void performTestCase(int testCase, String inputText, boolean expectedPass) {
		/************** Display an individual test case header **************/
		System.out.println("Test case: " + testCase);
		System.out.println("Input: \"" + inputText + "\"");
		
		/************** Call the recognizer to process the input **************/
		String resultText= EmailEvaluator.evaluateEmail(inputText);
		System.out.println();
		
		// If the resulting text is empty, the recognizer accepted the input
		if (resultText != "") {
			 // If the test case expected the test to pass then this is a failure
			if (expectedPass) {
				System.out.println("***Failure*** The email <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be valid, so this is a failure!\n");
				System.out.println("Error message: " + resultText);
				numFailed++;
			}
			// If the test case expected the test to fail then this is a success
			else {			
				System.out.println("***Success*** The email <" + inputText + "> is invalid." + 
						"\nBut it was supposed to be invalid, so this is a pass!\n");
				System.out.println("Error message: " + resultText);
				numPassed++;
			}
		}
		
		// If the resulting text is empty, the recognizer accepted the input
		else {	
			// If the test case expected the test to pass then this is a success
			if (expectedPass) {	
				System.out.println("***Success*** The email <" + inputText + 
						"> is valid, so this is a pass!");
				numPassed++;
			}
			// If the test case expected the test to fail then this is a failure
			else {
				System.out.println("***Failure*** The email <" + inputText + 
						"> was judged as valid" + 
						"\nBut it was supposed to be invalid, so this is a failure!");
				numFailed++;
			}
		}
		System.out.println("Evaluation:");
		System.out.println(resultText);
		System.out.println();
	}
}
