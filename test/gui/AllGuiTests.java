//package gui;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ RangeSetTest.class, ReliableRobotTest.class, UnreliableRobotTest.class, ReliableSensorTest.class,
	UnreliableSensorTest.class, WallFollowerTest.class, WizardTest.class })
public class AllGuiTests {

}
