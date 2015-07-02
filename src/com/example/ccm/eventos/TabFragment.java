package com.example.ccm.eventos;

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

import com.example.ccm.R;
import com.example.ccm.restclient.EventosRestClientTask;

public class TabFragment extends Fragment implements OnItemSelectedListener, OnItemClickListener {	
	
	
	private String idTipoAreaActual; 
	private String pageTitle;
	
	
	public TabFragment( String idTipoAreaActual, String pageTitle ){
		this.idTipoAreaActual = idTipoAreaActual;
		this.pageTitle = pageTitle;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		
		View vistaFragment = inflater.inflate( R.layout.lista_eventos, container, false );
		ListView listaEventos = (ListView) vistaFragment.findViewById( R.id.listview_lista_eventos );
		listaEventos.setOnItemClickListener( this );
		listaEventos.setOnItemSelectedListener( this );
		
		EventosRestClientTask eventosRestClientTask = new EventosRestClientTask( getActivity(), listaEventos );
		eventosRestClientTask.execute( idTipoAreaActual, pageTitle );
		
		return vistaFragment;
		
	}
	
	
	
	


	
	//====================================================== MÉTODOS DE OnItemSelectedListener===============================
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Log.v("Selected: " , String.valueOf(position) );
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}


	//====================================================== MÉTODOS DE OnItemClickListener==================================
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.v("Clicked: " , String.valueOf(position) );
	}
	
}
















