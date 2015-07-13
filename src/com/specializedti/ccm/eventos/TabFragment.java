package com.specializedti.ccm.eventos;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.specializedti.ccm.R;
import com.specializedti.ccm.restclient.CargaEventosRestClientTask;



public class TabFragment extends Fragment{	
	
	
	private String idTipoAreaActual; 
	private String pageTitle;
	
	private ListView listaEventos;
	
	
	public TabFragment( String idTipoAreaActual, String pageTitle ){
		this.idTipoAreaActual = idTipoAreaActual;
		this.pageTitle = pageTitle;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		//Log.i( "TabFragment", "OnCreateView" );
		View vistaFragment = inflater.inflate( R.layout.lista_eventos, container, false );
		listaEventos = (ListView) vistaFragment.findViewById( R.id.listview_lista_eventos );		
		return vistaFragment;		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//Log.i( "TabFragment", "OnPause" );
		/*
		CargaEventosRestClientTask cargaEventosRestClientTask = new CargaEventosRestClientTask( getActivity(), listaEventos );
		cargaEventosRestClientTask.execute( idTipoAreaActual, pageTitle );
		*/
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//Log.i( "TabFragment", "OnResume" );
		CargaEventosRestClientTask cargaEventosRestClientTask = new CargaEventosRestClientTask( getActivity(), listaEventos );
		cargaEventosRestClientTask.execute( idTipoAreaActual, pageTitle );
	}
	
}
















