package com.specializedti.ccm.eventos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.specializedti.ccm.R;
import com.specializedti.ccm.dummy.DummyContent;

/**
 * A fragment representing a single Area detail screen. This fragment is either
 * contained in a {@link AreaListActivity} in two-pane mode (on tablets) or a
 * {@link AreaDetailActivity} on handsets.
 */
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class AreaDetailFragment extends Fragment implements ActionBar.TabListener {
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
	
	private ViewPagerAdapter viewPagerAdapter;
	private ViewPager viewPager;
	
	
	private final ActionBar actionBar;
	public AreaDetailFragment( ActionBar actionBar ) {
		this.actionBar = actionBar;
	}
	
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu( true );
		/*
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get( getArguments().getString(ARG_ITEM_ID) );
		}
		*/
	}
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View vista = inflater.inflate( R.layout.view_pager, container, false );
		/*
		View vista = inflater.inflate( R.layout.tabhost, container, false );
		FragmentTabHost tabHost = (FragmentTabHost) vista.findViewById( android.R.id.tabhost );
		tabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
		
		tabHost.addTab( tabHost.newTabSpec("Tab_Lun").setIndicator("Lun"), TabFragment.class, null );
		tabHost.addTab( tabHost.newTabSpec("Tab_Mar").setIndicator("Mar"), TabFragment.class, null );
		tabHost.addTab( tabHost.newTabSpec("Tab_Mie").setIndicator("Mie"), TabFragment.class, null );
		tabHost.addTab( tabHost.newTabSpec("Tab_Jue").setIndicator("Jue"), TabFragment.class, null );
		tabHost.addTab( tabHost.newTabSpec("Tab_Vie").setIndicator("Vie"), TabFragment.class, null );
		*/
		String idTipoAreaActual = getArguments().getString( AreaDetailFragment.ARG_ITEM_ID );
		
		actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
		
		//Adaptador que retorna el fragment seleccionado
		viewPagerAdapter = new ViewPagerAdapter( getActivity().getApplicationContext(), idTipoAreaActual, getChildFragmentManager() );
		
		viewPager = (ViewPager) vista.findViewById( R.id.view_pager );
		viewPager.setAdapter( viewPagerAdapter );
		
		//Se agrega el Listener para controlar los eventos de cambio de tabs
		//Este listener es IMPORTANTE, de no haberlo al cambiar entre tabs no se actualza
		//la tab seleccionada en el actionbar
		viewPager.setOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener(){
			@Override
			public void onPageSelected( int position ){
				actionBar.setSelectedNavigationItem( position );
			}
		});
		//IMPORTANTE:
		//Cuando se despliega el patron Master/Detail en Tablets (Two-Pane-Mode)
		//La primera vez que se obtenga la supportActionBar(), no van a haber Tabs,
		//en este caso, cuando el numero de tabs es 0, se tienen que agregar.
		if ( actionBar.getTabCount() == 0 ){
			//actionBar.addTab( actionBar.newTab().setText(  getResources().getString( R.string.tab_lun )  ).setTabListener(this).setTag("Monday")    );
			actionBar.addTab( actionBar.newTab().setText(  getResources().getString( R.string.tab_mar )  ).setTabListener(this).setTag("Tuesday")   );
			actionBar.addTab( actionBar.newTab().setText(  getResources().getString( R.string.tab_mie )  ).setTabListener(this).setTag("Wednesday") );
			actionBar.addTab( actionBar.newTab().setText(  getResources().getString( R.string.tab_jue )  ).setTabListener(this).setTag("Thursday")  );
			actionBar.addTab( actionBar.newTab().setText(  getResources().getString( R.string.tab_vie )  ).setTabListener(this).setTag("Friday")    );
		}
		return vista;
	}
	
	
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
	}



	
	
	//=============================================== MÉTODOS DE ActionBar.TabListener =============================================
	@SuppressWarnings("deprecation")
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem( tab.getPosition() );		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {		
	}
	
	
	
	
}
