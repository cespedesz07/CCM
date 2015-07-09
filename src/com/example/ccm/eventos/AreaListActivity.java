package com.example.ccm.eventos;

import com.example.ccm.R;
import com.example.ccm.R.id;
import com.example.ccm.R.layout;
import com.example.ccm.actionbar.CCMActionBarActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * An activity representing a list of Areas. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link AreaDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link AreaListFragment} and the item details (if present) is a
 * {@link AreaDetailFragment}.
 * <p>
 * This activity also implements the required {@link AreaListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class AreaListActivity extends CCMActionBarActivity implements AreaListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_area_list);

		if (findViewById(R.id.area_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((AreaListFragment) getSupportFragmentManager().findFragmentById(R.id.area_list)).setActivateOnItemClick(true);
		}
		actionBar = getSupportActionBar();
	}
	
	
	
	
	
	//========================================== MÉTODOS DEL CALLBACK AreaListFragment.Callback ==================================

	/**
	 * Callback method from {@link AreaListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@SuppressLint("NewApi")
	@Override
	public void onItemSelected(String id) {
		//Log.v( "AreaListActivity Item Selected", id);
		if (mTwoPane) {
			// In two-pane mode (TABLET VIEW), show the detail view in this activity by
			// adding or replacing the detail fragment using a fragment transaction.
			AreaDetailFragment fragment = new AreaDetailFragment( this.actionBar );
			Bundle arguments = new Bundle();
			arguments.putString( AreaDetailFragment.ARG_ITEM_ID, id );
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.area_detail_container, fragment).commit();

		} 
		else {
			// In single-pane mode (DEVICE VIEW), simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent( this, AreaDetailActivity.class );
			detailIntent.putExtra( AreaDetailFragment.ARG_ITEM_ID, id );
			startActivity(detailIntent);
		}
	}
	
	public void onCreateOptionsMenu( Menu menu, MenuInflater menuInflater ){
		menuInflater.inflate(R.menu.ccmaction_bar, menu);
	}
	
}
