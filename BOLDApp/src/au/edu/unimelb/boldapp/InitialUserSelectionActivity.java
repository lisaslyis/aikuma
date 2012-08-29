package au.edu.unimelb.boldapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Activity that allows for the changing of currentUser, and links to the
 * CreateUserActivity.
 *
 * @author	Oliver Adams	<oliver.adams@gmail.com>
 * @author	Florian Hanke	<florian.hanke@gmail.com>
 */
public class InitialUserSelectionActivity extends ListActivity {
	/**
	 * Called when the activity is initially created.
	 *
	 * @param	savedInstanceState	Data the activity most recently supplied to
	 * onSaveInstanceState(Bundle).
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initial_user_selection);
	}

	/**
	 * Called when the activity starts.
	 */
	@Override
	public void onStart() {
		super.onStart();

		// Load users into file and set the list array adapter. Doing this in
		// this method ensures users that have just been created appear when
		// the current user returns from the CreateUserActivity
		FileIO.loadUsers();
		ArrayAdapter adapter = new UserArrayAdapter(this, 
				GlobalState.getUsers());
		setListAdapter(adapter);
	}

	/**
	 * When the list item is clicked.
	 *
	 * @param	l		the listview
	 * @param	v		the view that was clicked
	 * @param	positon	position in the list and array
	 * @param 	id		id
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		User user = (User) getListAdapter().getItem(position);
		GlobalState.setCurrentUser(user);
		Toast.makeText(this,
				user.getName() + " selected", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		this.finish();
	}

	/**
	 * Starts the CreateUserActivity
	 *
	 * @param	view	The button that was clicked.
	 */
	public void createUser(View view) {
		Intent intent = new Intent(this, CreateUserActivity.class);
		startActivity(intent);
	}
}