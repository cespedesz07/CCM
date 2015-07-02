package com.example.ccm.eventos;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

import com.example.ccm.R;

/**
 * An activity representing a single Area detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link AreaListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link AreaDetailFragment}.
 */
public class AreaDetailActivity extends ActionBarActivity implements ActionBar.TabListener {
	
	
	private ViewPagerAdapter viewPagerAdapter;
	private ViewPager viewPager;
	
	//Nombre del Tab actual devuelto por OnTabSelected()
	private String nombreTabSeleccionado;
	
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		setContentView(R.layout.activity_area_detail);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(AreaDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(AreaDetailFragment.ARG_ITEM_ID));
			AreaDetailFragment fragment = new AreaDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.area_detail_container, fragment).commit();
		}
		*/
		setContentView( R.layout.view_pager );
		
	
		String idTipoAreaActual = getIntent().getStringExtra( AreaDetailFragment.ARG_ITEM_ID );
		Log.v( "AreaDetailActivity.idTipoAreaActual", idTipoAreaActual );
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
		
		//Adaptador que retorna el fragment seleccionado
		viewPagerAdapter = new ViewPagerAdapter( this, idTipoAreaActual, getSupportFragmentManager() );
		
		viewPager = (ViewPager) findViewById( R.id.view_pager );
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
		
		actionBar.addTab( actionBar.newTab().setText(  getResources().getString( R.string.tab_lun )  ).setTabListener(this).setTag("Monday")    );
		actionBar.addTab( actionBar.newTab().setText(  getResources().getString( R.string.tab_mar )  ).setTabListener(this).setTag("Tuesday")   );
		actionBar.addTab( actionBar.newTab().setText(  getResources().getString( R.string.tab_mie )  ).setTabListener(this).setTag("Wednesday") );
		actionBar.addTab( actionBar.newTab().setText(  getResources().getString( R.string.tab_jue )  ).setTabListener(this).setTag("Thursday")  );
		actionBar.addTab( actionBar.newTab().setText(  getResources().getString( R.string.tab_vie )  ).setTabListener(this).setTag("Friday")    );
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ){
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.ccmaction_bar, menu);
		return true;
	}

	



	
	
	
	//=============================================== MÉTODOS DE ActionBar.TabListener =============================================
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
	}


	//Método que se ejecuta para desplegar el Tab seleccionado en el ViewPager
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
		nombreTabSeleccionado = String.valueOf( tab.getText() );
		viewPager.setCurrentItem( tab.getPosition() );
	}


	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		
	}
	
}
