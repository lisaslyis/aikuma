package au.edu.unimelb.boldapp;

import com.jayway.android.robotium.solo.Solo;
import android.test.ActivityInstrumentationTestCase2;

public class RobotiumTest extends
		ActivityInstrumentationTestCase2<InitialUserSelectionActivity> {

	private Solo solo;
	
	public RobotiumTest() {
		super("au.edu.unimelb.boldapp", InitialUserSelectionActivity.class);
	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}

	public void testCreateUser() {
		solo.clickOnButton("Create");
		solo.assertCurrentActivity("Not in CreateUserActivity",
				CreateUserActivity.class);
		solo.enterText(0, "Bären ausbinden");
		solo.clickOnImageButton(1);
		solo.assertCurrentActivity("Not in InitialUserSelectionActivity",
				InitialUserSelectionActivity.class);
		solo.clickInList(0);
		solo.assertCurrentActivity("Not in MainActivity",
				MainActivity.class);
		//assertTrue(solo.getText(0).getText().toString().equals("TestUser"));
		assertTrue(solo.getText("Bären ausbinden") != null);
	}

	public void testCreateEmptyUser() {
		solo.clickOnButton("Create");
		solo.assertCurrentActivity("Not in CreateUserActivity",
				CreateUserActivity.class);
		solo.enterText(0, "");
		solo.clickOnImageButton(1);
		solo.assertCurrentActivity("Not in CreateUserActivity",
				CreateUserActivity.class);
	}

	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}
}
