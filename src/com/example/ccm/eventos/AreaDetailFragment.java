package com.example.ccm.eventos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ccm.R;
import com.example.ccm.dummy.DummyContent;

/**
 * A fragment representing a single Area detail screen. This fragment is either
 * contained in a {@link AreaListActivity} in two-pane mode (on tablets) or a
 * {@link AreaDetailActivity} on handsets.
 */
public class AreaDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	
	
	
	public AreaDetailFragment() {
	}
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu( true );
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get( getArguments().getString(ARG_ITEM_ID) );
		}
	}
	
	@Override 
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
		super.onCreateOptionsMenu(menu, menuInflater);
		menuInflater.inflate(R.menu.ccmaction_bar, menu);
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if ( id == R.id.action_salir ){
			Toast.makeText(getActivity(), "Closing", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View vista = inflater.inflate( R.layout.tabhost, container, false );
		
		FragmentTabHost tabHost = (FragmentTabHost) vista.findViewById( android.R.id.tabhost );
		tabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
		
		tabHost.addTab( tabHost.newTabSpec("Tab_Lun").setIndicator("Lun"), TabFragment.class, null );
		tabHost.addTab( tabHost.newTabSpec("Tab_Mar").setIndicator("Mar"), TabFragment.class, null );
		tabHost.addTab( tabHost.newTabSpec("Tab_Mie").setIndicator("Mie"), TabFragment.class, null );
		tabHost.addTab( tabHost.newTabSpec("Tab_Jue").setIndicator("Jue"), TabFragment.class, null );
		tabHost.addTab( tabHost.newTabSpec("Tab_Vie").setIndicator("Vie"), TabFragment.class, null );
		
		return vista;
	}
	
	
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
	}
}
