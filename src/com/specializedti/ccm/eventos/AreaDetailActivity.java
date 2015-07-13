package com.specializedti.ccm.eventos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
/*
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
*/
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

import com.specializedti.ccm.R;
import com.specializedti.ccm.actionbar.CCMActionBarActivity;

/**
 * An activity representing a single Area detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link AreaListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link AreaDetailFragment}.
 */
@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class AreaDetailActivity extends CCMActionBarActivity{
	
	
	private ViewPagerAdapter viewPagerAdapter;
	private ViewPager viewPager;
	
	

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		// savedInstanceState es != a null cuando hay un fragment
		// guardado de configuraciones anteriores a esta actividad
		// (e.g cuando se rota la pantalla de portrait a landscape)
		// en este caso, el fragment será re-añadido automáticamente
		// a su container, de manera que no se tenga que añadir manualmente
		setContentView(R.layout.activity_area_detail);
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(AreaDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(AreaDetailFragment.ARG_ITEM_ID));
			AreaDetailFragment fragment = new AreaDetailFragment( getSupportActionBar() );
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.area_detail_container, fragment).commit();
		}
		/*

		//AL COMENTAR EL CODIGO ANTERIOR Y DESCOMENTAR EL SIGUIENTE, SE MUESTRA LA FUNCIONALIDAD DE SWIPEABLE TABS DENTRO DEL VIEW PAGER
		//PERO SOLAMENTE EN SINGLE-PANE-MODE, EN TWO PANE MODE NO MUESTRA NADA YA QUE NO SE ESTARIA
		//DESPLEGANDO LOS FRAGMENTS
		
		setContentView( R.layout.view_pager );
		String idTipoAreaActual = getIntent().getStringExtra( AreaDetailFragment.ARG_ITEM_ID );
		//Log.v( "AreaDetailActivity.idTipoAreaActual", idTipoAreaActual );
		
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
		*/
	}	
	
	
	//=============================================== MÉTODOS DE ActionBar.TabListener =============================================
	/*
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
	}


	//Método que se ejecuta para desplegar el Tab seleccionado en el ViewPager
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
		viewPager.setCurrentItem( tab.getPosition() );
	}


	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		
	}
	*/
	
}
