/*
 * HAVE NOT FIGURED OUT HOW TO GET THIS TO WORK, WILL JUST IGNORE FOR NOW
 */

package gui;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class RunAllTests {
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(AllGuiTests.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
		System.out.println(result.wasSuccessful());
	}
}
