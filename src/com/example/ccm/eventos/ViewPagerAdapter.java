package com.example.ccm.eventos;

import com.example.ccm.R;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	
	private Context context;
	private String idTipoAreaActual;
	private FragmentManager childFragmentManager;

	public ViewPagerAdapter( Context context, String idTipoAreaActual, FragmentManager childFramentManager) {
		super( childFramentManager );
		this.idTipoAreaActual = idTipoAreaActual;
		this.context = context;
	}
	
	//IMPORTANTE: El método getItem() NO ES INVOCADO cada vez que se cambia entre tabs
	//sino que es llamado para poder desplegar las tabs, si una tab ya ha sido desplegada
	//no se vuelve a llamar el método.
	// Ejemplo: Al inicio de la ejecucion de la app, se invoca getItem() 2 veces para desplegar
	// el primer y el segundo tab, cuando se mueve al segundo tab, se invoca getItem() para cargar el tercero
	@Override
	public Fragment getItem(int position) {
		//Log.v( "ViewPagerAdapter.getItem()" , String.valueOf(getPageTitle(position)) );
		String pageTitle = String.valueOf( getPageTitle( position ) );
		return new TabFragment( idTipoAreaActual, pageTitle );
	}
	

	@Override
	public int getCount() {		
		return 4;
	}
	
	
	@Override
	public CharSequence getPageTitle( int position ){
		super.getPageTitle(position);
		String tabMar = "Tuesday";
		String tabMie = "Wednesday";
		String tabJue = "Thursday";
		String tabVie = "Friday";
		String[] pageTabs = { tabMar, tabMie, tabJue, tabVie };
		return pageTabs[position];
	}
}
